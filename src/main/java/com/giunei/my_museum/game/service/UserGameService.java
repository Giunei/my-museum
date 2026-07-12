package com.giunei.my_museum.game.service;

import com.giunei.my_museum.achievement.service.UserGoalService;
import com.giunei.my_museum.common.exception.AccessDeniedException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.game.dto.AddGameRequest;
import com.giunei.my_museum.game.dto.GameSummaryResponse;
import com.giunei.my_museum.game.dto.StoreInfo;
import com.giunei.my_museum.game.dto.UpdateUserGameRequest;
import com.giunei.my_museum.game.dto.UserGameResponse;
import com.giunei.my_museum.game.entity.GameCatalog;
import com.giunei.my_museum.game.entity.UserGame;
import com.giunei.my_museum.game.enums.GameSort;
import com.giunei.my_museum.game.repository.UserGameRepository;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGameService {

    private final GameCatalogService gameCatalogService;
    private final UserGameRepository userGameRepository;
    private final UserMediaRepository userMediaRepository;
    private final RawgStoreService rawgStoreService;
    private final UserGoalService goalService;

    @Transactional
    public void addGameToCollection(AddGameRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        GameCatalog gameCatalog = gameCatalogService.findOrCreateById(request.rawgId());

        UserMedia media = userMediaRepository.findByUserAndExternalId(user, gameCatalog.getRawgId().toString())
                .orElseGet(UserMedia::new);

        media.setUser(user);
        media.setType(MediaType.GAME);
        media.setExternalId(gameCatalog.getRawgId().toString());
        media.setTitle(gameCatalog.getName());
        applyProgressToMedia(media, request.status(), null);

        media = userMediaRepository.save(media);

        UserGame userGame = userGameRepository.findByMediaId(media.getId())
                .orElseGet(UserGame::new);

        userGame.setMedia(media);
        userGame.setSteamAppId(null);
        userGame.setPlaytimeMinutes(0);
        userGame.setAchievementsUnlocked(0);
        userGame.setTotalAchievements(0);
        userGame.setPlatinumed(request.platinumed());
        userGame.setStatus(media.getStatus());
        userGame.setRawgId(gameCatalog.getRawgId());
        userGame.setName(gameCatalog.getName());
        userGame.setGenres(null);
        userGame.setPlatforms(null);
        userGame.setStores(rawgStoreService.resolveStoreLinksByRawgId(gameCatalog.getRawgId(), null));

        userGameRepository.save(userGame);
    }

    @Transactional
    public void ensureLinkedFromMedia(UserMedia media) {
        linkMediaAsUserGame(media);
    }

    private void linkMediaAsUserGame(UserMedia media) {
        if (media == null || media.getType() != MediaType.GAME || media.getId() == null) {
            return;
        }

        UserGame userGame = userGameRepository.findByMediaId(media.getId())
                .orElseGet(UserGame::new);

        userGame.setMedia(media);
        applyDefaultCounters(userGame);
        userGame.setStatus(media.getStatus() != null ? media.getStatus() : MediaStatus.PENDING);
        applyIdentityFromMedia(userGame, media);

        userGameRepository.save(userGame);
    }

    private void applyDefaultCounters(UserGame userGame) {
        if (userGame.getPlaytimeMinutes() == null) {
            userGame.setPlaytimeMinutes(0);
        }
        if (userGame.getAchievementsUnlocked() == null) {
            userGame.setAchievementsUnlocked(0);
        }
        if (userGame.getTotalAchievements() == null) {
            userGame.setTotalAchievements(0);
        }
    }

    private void applyIdentityFromMedia(UserGame userGame, UserMedia media) {
        Long rawgId = parseRawgId(media.getExternalId());
        if (rawgId == null) {
            if (userGame.getName() == null || userGame.getName().isBlank()) {
                userGame.setName(media.getTitle());
            }
            return;
        }

        userGame.setRawgId(rawgId);
        GameCatalog catalog = gameCatalogService.findOrCreateById(rawgId);
        String name = resolveGameName(catalog, media.getTitle());
        userGame.setName(name);

        if (media.getTitle() == null || media.getTitle().isBlank()) {
            media.setTitle(name);
            userMediaRepository.save(media);
        }

        if (userGame.getStores() == null || userGame.getStores().isEmpty()) {
            userGame.setStores(rawgStoreService.resolveStoreLinksByRawgId(rawgId, userGame.getSteamAppId()));
        }
    }

    private String resolveGameName(GameCatalog catalog, String fallbackTitle) {
        if (catalog.getName() != null && !"Unknown Game".equals(catalog.getName())) {
            return catalog.getName();
        }
        return fallbackTitle;
    }

    private void repairOrphanGameMedia(User user) {
        userMediaRepository.findByUserAndType(user, MediaType.GAME, Pageable.unpaged())
                .forEach(media -> {
                    if (userGameRepository.findByMediaId(media.getId()).isEmpty()) {
                        linkMediaAsUserGame(media);
                    }
                });
    }

    private Long parseRawgId(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(externalId.trim());
        } catch (NumberFormatException _) {
            return null;
        }
    }

    @Transactional
    public void updateGame(Long id, UpdateUserGameRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!userGame.getMedia().getUser().equals(user)) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este jogo");
        }

        UserMedia media = userGame.getMedia();
        LocalDate previousFinishedAt = media.getFinishedAt();
        MediaStatus previousStatus = userGame.getStatus();

        if (request.rating() != null) {
            media.setRating(request.rating());
        }

        if (request.finishedAt() != null) {
            media.setFinishedAt(request.finishedAt());
            applyProgressToMedia(media, MediaStatus.COMPLETED, request.finishedAt());
            userGame.setStatus(MediaStatus.COMPLETED);
        }

        if (request.highlighted() != null) {
            media.setHighlighted(request.highlighted());
        }

        if (request.status() != null) {
            applyProgressToMedia(media, request.status(), request.finishedAt());
            userGame.setStatus(media.getStatus());
        }

        if (request.platinumed() != null) {
            userGame.setPlatinumed(request.platinumed());
        }

        userMediaRepository.save(media);
        userGameRepository.save(userGame);

        if (!Objects.equals(previousFinishedAt, media.getFinishedAt())
                || previousStatus != userGame.getStatus()
                || previousFinishedAt != null && media.getFinishedAt() == null) {
            goalService.recalculateProgress(user, MediaType.GAME);
        }
    }

    private void applyProgressToMedia(UserMedia media, MediaStatus status, LocalDate finishedAt) {
        MediaStatus resolved = status != null ? status : MediaStatus.PENDING;
        media.setStatus(resolved);

        if (resolved == MediaStatus.COMPLETED) {
            media.setCompleted(true);
            if (finishedAt != null) {
                media.setFinishedAt(finishedAt);
            }
            return;
        }

        media.setCompleted(false);
        media.setFinishedAt(null);
    }

    @Transactional
    public List<UserGameResponse> listHighlightedGames() {
        return buildHighlightedGames(SecurityUtils.getAuthenticatedUser());
    }

    @Transactional
    public List<UserGameResponse> listHighlightedGames(User user) {
        return buildHighlightedGames(user);
    }

    private List<UserGameResponse> buildHighlightedGames(User user) {
        repairOrphanGameMedia(user);
        return userGameRepository.findHighlightedWithMediaByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<UserGameResponse> listAllGames(MediaStatus statusFilter, GameSort sort) {
        return buildAllGames(SecurityUtils.getAuthenticatedUser(), statusFilter, sort);
    }

    @Transactional
    public List<UserGameResponse> listAllGames(User user, MediaStatus statusFilter, GameSort sort) {
        return buildAllGames(user, statusFilter, sort);
    }

    private List<UserGameResponse> buildAllGames(User user, MediaStatus statusFilter, GameSort sort) {
        repairOrphanGameMedia(user);
        List<UserGame> games = statusFilter == null
                ? userGameRepository.findAllWithMediaByUserId(user.getId())
                : userGameRepository.findAllWithMediaByUserIdAndStatus(user.getId(), statusFilter);

        return applySort(games, sort).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> listMostPlayedGames(int limit) {
        return buildMostPlayedGames(SecurityUtils.getAuthenticatedUser(), limit);
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> listMostPlayedGames(User user, int limit) {
        return buildMostPlayedGames(user, limit);
    }

    private List<UserGameResponse> buildMostPlayedGames(User user, int limit) {
        return userGameRepository.findAllWithMediaByUserIdOrderByPlaytimeDesc(user.getId()).stream()
                .limit(limit)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameSummaryResponse getSummary() {
        return buildSummary(SecurityUtils.getAuthenticatedUser());
    }

    @Transactional(readOnly = true)
    public GameSummaryResponse getSummary(User user) {
        return buildSummary(user);
    }

    private GameSummaryResponse buildSummary(User user) {
        List<UserGame> allGames = userGameRepository.findAllWithMediaByUserId(user.getId());
        int totalGames = allGames.size();

        List<UserGame> completedGames = userGameRepository.findAllWithMediaByUserIdAndStatus(
                user.getId(),
                MediaStatus.COMPLETED
        );
        int completedCount = completedGames.size();

        int totalPlaytimeMinutes = allGames.stream()
                .mapToInt(g -> g.getPlaytimeMinutes() != null ? g.getPlaytimeMinutes() : 0)
                .sum();
        double totalPlaytimeHours = totalPlaytimeMinutes / 60.0;

        List<String> favoriteGenres = allGames.stream()
                .filter(g -> g.getGenres() != null)
                .flatMap(g -> g.getGenres().stream())
                .collect(Collectors.toList());

        return new GameSummaryResponse(totalGames, completedCount, totalPlaytimeHours, favoriteGenres);
    }

    private List<UserGame> applySort(List<UserGame> games, GameSort sort) {
        if (sort == null) {
            return games;
        }

        return switch (sort) {
            case PLAYTIME -> games.stream()
                    .sorted((a, b) -> Integer.compare(
                            b.getPlaytimeMinutes() != null ? b.getPlaytimeMinutes() : 0,
                            a.getPlaytimeMinutes() != null ? a.getPlaytimeMinutes() : 0
                    ))
                    .toList();
            case HIGHEST_RATED -> games.stream()
                    .sorted((a, b) -> Integer.compare(
                            b.getAchievementsUnlocked() != null ? b.getAchievementsUnlocked() : 0,
                            a.getAchievementsUnlocked() != null ? a.getAchievementsUnlocked() : 0
                    ))
                    .toList();
            case ALPHABETICAL -> games.stream()
                    .sorted((a, b) -> {
                        String aName = a.getName() != null ? a.getName() : "";
                        String bName = b.getName() != null ? b.getName() : "";
                        return aName.compareToIgnoreCase(bName);
                    })
                    .toList();
        };
    }

    private UserGameResponse toResponse(UserGame userGame) {
        UserMedia media = userGame.getMedia();
        if (media == null) {
            throw new IllegalStateException("UserGame " + userGame.getId() + " is not linked to user media");
        }

        String name = userGame.getName() != null ? userGame.getName() : media.getTitle();
        List<StoreInfo> stores = rawgStoreService.applySteamFallback(
                userGame.getStores(),
                userGame.getSteamAppId()
        );

        return new UserGameResponse(
                userGame.getId(),
                media.getId(),
                name,
                media.getThumbnail(),
                userGame.getStatus(),
                userGame.isPlatinumed(),
                userGame.getPlaytimeMinutes(),
                userGame.getAchievementsUnlocked(),
                userGame.getTotalAchievements(),
                media.isHighlighted(),
                media.getDisplayOrder(),
                userGame.getGenres(),
                userGame.getPlatforms(),
                stores
        );
    }
}
