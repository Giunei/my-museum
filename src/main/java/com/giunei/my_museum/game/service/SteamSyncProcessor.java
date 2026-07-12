package com.giunei.my_museum.game.service;

import com.giunei.my_museum.game.client.RawgClient;
import com.giunei.my_museum.game.client.SteamClient;
import com.giunei.my_museum.game.dto.RawgGameResponse;
import com.giunei.my_museum.game.dto.SteamAchievementsResponse;
import com.giunei.my_museum.game.dto.SteamOwnedGamesResponse;
import com.giunei.my_museum.game.dto.StoreInfo;
import com.giunei.my_museum.game.entity.SteamAccount;
import com.giunei.my_museum.game.entity.UserGame;
import com.giunei.my_museum.game.repository.UserGameRepository;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamSyncProcessor {

    private record ExistingGameData(
            Map<String, UserMedia> mediaByExternalId,
            Map<String, UserGame> gameBySteamAppId,
            Map<Long, UserGame> gameByRawgId,
            Map<String, UserGame> gameByNormalizedName
    ) {
    }

    private record AchievementData(
            Integer unlocked,
            Integer total,
            boolean platinumed
    ) {
        static AchievementData empty() {
            return new AchievementData(null, null, false);
        }
    }

    private record RawgMetadata(
            String name,
            Long rawgId,
            String coverUrl,
            List<String> genres,
            List<String> platforms,
            List<StoreInfo> stores,
            boolean exactMatch
    ) {
    }

    private record EnrichedGame(
            String appId,
            String steamName,
            int playtimeMinutes,
            Integer achievementsUnlocked,
            Integer totalAchievements,
            boolean platinumed,
            RawgMetadata rawgMetadata,
            String steamThumbnail
    ) {
    }

    private final SteamClient steamClient;
    private final RawgClient rawgClient;
    private final RawgStoreService rawgStoreService;
    private final SteamService steamService;
    private final UserMediaRepository mediaRepository;
    private final UserGameRepository gameRepository;
    private final UserRepository userRepository;
    private final SteamSyncStatusService statusService;
    private final ExecutorService steamSyncEnrichmentExecutor;
    private final PlatformTransactionManager transactionManager;

    public void runSync(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SteamAccount account = steamService.getAccount(user);
        SteamOwnedGamesResponse response = steamClient.getOwnedGames(account.getSteamId64());

        if (response == null || response.response() == null || response.response().games() == null) {
            statusService.failSync(userId, "Nenhum jogo encontrado na biblioteca Steam");
            return;
        }

        List<SteamOwnedGamesResponse.Game> games = response.response().games();
        statusService.startSync(userId, games.size());

        ExistingGameData existing = readExistingData(user);
        List<EnrichedGame> enrichedGames = enrichGames(userId, account, games);
        persistGames(user, existing, enrichedGames);

        statusService.completeSync(userId);
        log.info("Steam sync completed for user {}: {}/{} games persisted",
                userId, enrichedGames.size(), games.size());
    }

    private ExistingGameData readExistingData(User user) {
        TransactionTemplate readTx = new TransactionTemplate(transactionManager);
        readTx.setReadOnly(true);

        return readTx.execute(status -> {
            Map<String, UserMedia> mediaByExternalId = mediaRepository
                    .findByUserAndType(user, MediaType.GAME, Pageable.unpaged())
                    .stream()
                    .filter(media -> media.getExternalId() != null)
                    .collect(Collectors.toMap(UserMedia::getExternalId, Function.identity(), (left, right) -> left));

            List<UserGame> existingGames = gameRepository.findAllWithMediaByUserId(user.getId());

            Map<String, UserGame> gameBySteamAppId = existingGames.stream()
                    .filter(game -> game.getSteamAppId() != null && !game.getSteamAppId().isBlank())
                    .collect(Collectors.toMap(UserGame::getSteamAppId, Function.identity(), (left, right) -> left));

            Map<Long, UserGame> gameByRawgId = existingGames.stream()
                    .filter(game -> game.getRawgId() != null)
                    .collect(Collectors.toMap(UserGame::getRawgId, Function.identity(), (left, right) -> left));

            Map<String, UserGame> gameByNormalizedName = new HashMap<>();
            for (UserGame game : existingGames) {
                String key = normalizeTitle(game.getName());
                if (key == null && game.getMedia() != null) {
                    key = normalizeTitle(game.getMedia().getTitle());
                }
                if (key != null) {
                    gameByNormalizedName.putIfAbsent(key, game);
                }
            }

            return new ExistingGameData(mediaByExternalId, gameBySteamAppId, gameByRawgId, gameByNormalizedName);
        });
    }

    private List<EnrichedGame> enrichGames(
            Long userId,
            SteamAccount account,
            List<SteamOwnedGamesResponse.Game> games
    ) {
        AtomicInteger completedCount = new AtomicInteger(0);

        List<CompletableFuture<Optional<EnrichedGame>>> futures = games.stream()
                .map(game -> CompletableFuture.supplyAsync(
                        () -> enrichSingleGame(userId, account, game, completedCount),
                        steamSyncEnrichmentExecutor
                ))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<EnrichedGame> enrichSingleGame(
            Long userId,
            SteamAccount account,
            SteamOwnedGamesResponse.Game game,
            AtomicInteger completedCount
    ) {
        try {
            AchievementData achievements = fetchAchievements(
                    account.getSteamId64(),
                    game.appid().toString(),
                    game.has_community_visible_stats()
            );
            RawgMetadata rawgMetadata = fetchRawgMetadata(game.name(), game.appid().toString());
            String steamThumbnail = game.img_icon_url() != null 
                    ? buildSteamThumbnailUrl(game.appid().toString()) 
                    : null;

            EnrichedGame enriched = new EnrichedGame(
                    game.appid().toString(),
                    game.name(),
                    game.playtime_forever() != null ? game.playtime_forever() : 0,
                    achievements.unlocked(),
                    achievements.total(),
                    achievements.platinumed(),
                    rawgMetadata,
                    steamThumbnail
            );

            int completed = completedCount.incrementAndGet();
            statusService.updateProgress(userId, completed, "Sincronizando: " + game.name());
            return Optional.of(enriched);
        } catch (Exception e) {
            log.error("Error syncing game: {}", game.name(), e);
            int completed = completedCount.incrementAndGet();
            statusService.updateProgress(userId, completed, "Erro em: " + game.name());
            return Optional.empty();
        }
    }

    private String buildSteamThumbnailUrl(String imgIconUrl) {
        return "https://cdn.cloudflare.steamstatic.com/steam/apps/" + imgIconUrl + "/header.jpg";
    }

    private AchievementData fetchAchievements(String steamId, String appId, Boolean hasStats) {
        if (hasStats == null || !hasStats) {
            return AchievementData.empty();
        }

        try {
            SteamAchievementsResponse response = steamClient.getAchievements(steamId, appId);
            return parseAchievements(response);
        } catch (Exception e) {
            log.warn("Failed to fetch achievements for app {}: {}", appId, e.getMessage());
            return AchievementData.empty();
        }
    }

    private AchievementData parseAchievements(SteamAchievementsResponse response) {
        if (response == null || response.playerstats() == null) {
            return AchievementData.empty();
        }

        SteamAchievementsResponse.PlayerStats stats = response.playerstats();
        List<SteamAchievementsResponse.Achievement> achievements = stats.achievements();
        Integer total = achievements != null ? achievements.size() : null;

        Integer unlocked = stats.achievements_unlocked();
        if (unlocked == null && achievements != null) {
            unlocked = (int) achievements.stream()
                    .filter(achievement -> achievement.achieved() != null && achievement.achieved() == 1)
                    .count();
        }

        boolean platinumed = total != null
                && total > 0
                && unlocked != null
                && unlocked.equals(total);

        return new AchievementData(unlocked, total, platinumed);
    }

    private RawgMetadata fetchRawgMetadata(String gameName, String steamAppId) {
        try {
            RawgGameResponse response = rawgClient.searchGames(gameName, 1, 1);
            if (response == null || response.results() == null || response.results().isEmpty()) {
                return new RawgMetadata(
                        gameName, null, null, List.of(), List.of(),
                        rawgStoreService.applySteamFallback(null, steamAppId),
                        false
                );
            }

            RawgGameResponse.RawgGameItem item = response.results().get(0);
            boolean exactMatch = gameName.equalsIgnoreCase(item.name());

            return new RawgMetadata(
                    item.name() != null ? item.name() : gameName,
                    item.id() != null ? item.id().longValue() : null,
                    item.backgroundImage(),
                    extractGenreNames(item.genres()),
                    extractPlatformNames(item.platforms()),
                    rawgStoreService.resolveStoreInfos(item, steamAppId),
                    exactMatch
            );
        } catch (Exception e) {
            log.warn("Failed to fetch RAWG data for {}: {}", gameName, e.getMessage());
            return new RawgMetadata(
                    gameName, null, null, List.of(), List.of(),
                    rawgStoreService.applySteamFallback(null, steamAppId),
                    false
            );
        }
    }

    private void persistGames(User user, ExistingGameData existing, List<EnrichedGame> enrichedGames) {
        TransactionTemplate writeTx = new TransactionTemplate(transactionManager);
        writeTx.executeWithoutResult(status -> {
            List<UserMedia> mediaToSave = new ArrayList<>();
            List<UserGame> gamesToSave = new ArrayList<>();

            for (EnrichedGame enriched : enrichedGames) {
                prepareGameForPersist(user, existing, enriched, mediaToSave, gamesToSave);
            }

            if (!mediaToSave.isEmpty()) {
                mediaRepository.saveAll(mediaToSave);
            }
            if (!gamesToSave.isEmpty()) {
                gameRepository.saveAll(gamesToSave);
            }
        });
    }

    private void prepareGameForPersist(
            User user,
            ExistingGameData existing,
            EnrichedGame enriched,
            List<UserMedia> mediaToSave,
            List<UserGame> gamesToSave
    ) {
        UserGame userGame = resolveUserGame(existing, enriched);
        UserMedia media = resolveMedia(existing, userGame, enriched);

        media.setUser(user);
        media.setType(MediaType.GAME);
        assignExternalIdIfMissing(media, enriched);
        assignTitleIfMissing(media, enriched.steamName());
        applyImportDefaultsIfNew(media, userGame);

        userGame.setMedia(media);
        userGame.setSteamAppId(enriched.appId());
        userGame.setPlaytimeMinutes(enriched.playtimeMinutes());
        userGame.setAchievementsUnlocked(enriched.achievementsUnlocked());
        userGame.setTotalAchievements(enriched.totalAchievements());
        userGame.setPlatinumed(enriched.platinumed());
        applyGameMetadata(userGame, enriched);

        mediaToSave.add(media);
        gamesToSave.add(userGame);
    }

    private void applyImportDefaultsIfNew(UserMedia media, UserGame userGame) {
        boolean newMedia = media.getId() == null;
        if (newMedia) {
            media.setStatus(MediaStatus.PENDING);
            media.setCompleted(false);
            media.setFinishedAt(null);
            media.setRating(null);
        } else if (media.getStatus() == null) {
            media.setStatus(MediaStatus.PENDING);
            media.setCompleted(false);
        }

        if (userGame.getStatus() == null) {
            userGame.setStatus(media.getStatus() != null ? media.getStatus() : MediaStatus.PENDING);
        }
    }

    private void assignExternalIdIfMissing(UserMedia media, EnrichedGame enriched) {
        if (media.getExternalId() != null && !media.getExternalId().isBlank()) {
            return;
        }
        RawgMetadata rawg = enriched.rawgMetadata();
        if (rawg.exactMatch() && rawg.rawgId() != null) {
            media.setExternalId(String.valueOf(rawg.rawgId()));
            return;
        }
        media.setExternalId(enriched.appId());
    }

    private void assignTitleIfMissing(UserMedia media, String steamName) {
        if (media.getTitle() == null || media.getTitle().isBlank()) {
            media.setTitle(steamName);
        }
    }

    private UserGame resolveUserGame(ExistingGameData existing, EnrichedGame enriched) {
        UserGame bySteam = existing.gameBySteamAppId().get(enriched.appId());
        if (bySteam != null) {
            return bySteam;
        }

        if (enriched.rawgMetadata().exactMatch() && enriched.rawgMetadata().rawgId() != null) {
            UserGame byRawg = existing.gameByRawgId().get(enriched.rawgMetadata().rawgId());
            if (byRawg != null) {
                return byRawg;
            }
        }

        UserGame byName = existing.gameByNormalizedName().get(normalizeTitle(enriched.steamName()));
        return byName != null ? byName : new UserGame();
    }

    private UserMedia resolveMedia(ExistingGameData existing, UserGame userGame, EnrichedGame enriched) {
        if (userGame.getMedia() != null && userGame.getMedia().getId() != null) {
            return userGame.getMedia();
        }

        UserMedia bySteamExternalId = existing.mediaByExternalId().get(enriched.appId());
        if (bySteamExternalId != null) {
            return bySteamExternalId;
        }

        if (enriched.rawgMetadata().exactMatch() && enriched.rawgMetadata().rawgId() != null) {
            UserMedia byRawgExternalId = existing.mediaByExternalId()
                    .get(String.valueOf(enriched.rawgMetadata().rawgId()));
            if (byRawgExternalId != null) {
                return byRawgExternalId;
            }
        }

        return new UserMedia();
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        return title.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    private void applyGameMetadata(UserGame userGame, EnrichedGame enriched) {
        RawgMetadata rawg = enriched.rawgMetadata();

        if (!rawg.exactMatch()) {
            userGame.setRawgId(null);
            userGame.setName(enriched.steamName());
            userGame.setGenres(List.of());
            userGame.setPlatforms(List.of());
            userGame.setStores(rawgStoreService.applySteamFallback(null, enriched.appId()));
            userGame.getMedia().setThumbnail(enriched.steamThumbnail());
            log.info("Using Steam data for {} (RAWG match was inexact: {})",
                    enriched.steamName(), rawg.name());
            return;
        }

        userGame.setRawgId(rawg.rawgId());
        userGame.setName(rawg.name());
        userGame.setGenres(rawg.genres());
        userGame.setPlatforms(rawg.platforms());
        userGame.setStores(rawg.stores());
        userGame.getMedia().setThumbnail(
                enriched.steamThumbnail() != null ? enriched.steamThumbnail() : rawg.coverUrl()
        );
    }

    private List<String> extractGenreNames(List<RawgGameResponse.RawgGenre> genres) {
        if (genres == null || genres.isEmpty()) {
            return List.of();
        }
        return genres.stream()
                .map(RawgGameResponse.RawgGenre::name)
                .toList();
    }

    private List<String> extractPlatformNames(List<RawgGameResponse.RawgPlatform> platforms) {
        if (platforms == null || platforms.isEmpty()) {
            return List.of();
        }
        return platforms.stream()
                .map(RawgGameResponse.RawgPlatform::platform)
                .map(RawgGameResponse.RawgPlatform.RawgPlatformInfo::name)
                .toList();
    }
}
