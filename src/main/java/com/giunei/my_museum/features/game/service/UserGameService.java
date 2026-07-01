package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.game.dto.AddGameRequest;
import com.giunei.my_museum.features.game.dto.GameSummaryResponse;
import com.giunei.my_museum.features.game.dto.StoreInfo;
import com.giunei.my_museum.features.game.dto.UpdateUserGameRequest;
import com.giunei.my_museum.features.game.dto.UserGameResponse;
import com.giunei.my_museum.features.game.entity.GameCatalog;
import com.giunei.my_museum.features.game.entity.UserGame;
import com.giunei.my_museum.features.game.enums.GameSort;
import com.giunei.my_museum.features.game.repository.UserGameRepository;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGameService {

    private final GameCatalogService gameCatalogService;
    private final UserGameRepository userGameRepository;
    private final UserMediaRepository userMediaRepository;
    private final RawgStoreService rawgStoreService;

    @Transactional
    public void addGameToCollection(AddGameRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        GameCatalog gameCatalog = gameCatalogService.findOrCreateById(request.rawgId());

        UserMedia media = userMediaRepository.findByUserAndExternalId(user, gameCatalog.getRawgId().toString())
                .orElseGet(UserMedia::new);

        media.setUser(user);
        media.setType(MediaType.GAME);
        media.setExternalId(gameCatalog.getRawgId().toString());

        if (request.status() == MediaStatus.COMPLETED) {
            media.setCompleted(true);
        }

        media = userMediaRepository.save(media);

        UserGame userGame = userGameRepository.findByMediaId(media.getId())
                .orElseGet(UserGame::new);

        userGame.setMedia(media);
        userGame.setSteamAppId(null);
        userGame.setPlaytimeMinutes(0);
        userGame.setAchievementsUnlocked(0);
        userGame.setTotalAchievements(0);
        userGame.setPlatinumed(request.platinumed());
        userGame.setStatus(request.status());
        userGame.setRawgId(gameCatalog.getRawgId());
        userGame.setName(gameCatalog.getName());
        userGame.setGenres(null);
        userGame.setPlatforms(null);
        userGame.setStores(rawgStoreService.resolveStoreLinksByRawgId(gameCatalog.getRawgId(), null));

        userGameRepository.save(userGame);
    }

    @Transactional
    public void updateGame(Long id, UpdateUserGameRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Verify ownership
        if (!userGame.getMedia().getUser().equals(user)) {
            throw new RuntimeException("You don't have permission to update this game");
        }

        UserMedia media = userGame.getMedia();

        if (request.rating() != null) {
            media.setRating(request.rating());
        }

        if (request.finishedAt() != null) {
            media.setFinishedAt(request.finishedAt());
            media.setCompleted(true);
        }

        if (request.highlighted() != null) {
            media.setHighlighted(request.highlighted());
        }

        if (request.status() != null) {
            userGame.setStatus(request.status());
            if (request.status() == MediaStatus.COMPLETED) {
                media.setCompleted(true);
            }
        }

        if (request.platinumed() != null) {
            userGame.setPlatinumed(request.platinumed());
        }

        userMediaRepository.save(media);
        userGameRepository.save(userGame);
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> listHighlightedGames() {
        User user = SecurityUtils.getAuthenticatedUser();
        return userGameRepository.findHighlightedWithMediaByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> listAllGames(MediaStatus statusFilter, GameSort sort) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<UserGame> games;

        if (statusFilter == null) {
            games = userGameRepository.findAllWithMediaByUserId(user.getId());
        } else {
            games = userGameRepository.findAllWithMediaByUserIdAndStatus(user.getId(), statusFilter);
        }

        return applySort(games, sort).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> listMostPlayedGames(int limit) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<UserGame> games = userGameRepository.findAllWithMediaByUserIdOrderByPlaytimeDesc(user.getId());

        return games.stream()
                .limit(limit)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameSummaryResponse getSummary() {
        User user = SecurityUtils.getAuthenticatedUser();

        List<UserGame> allGames = userGameRepository.findAllWithMediaByUserId(user.getId());
        int totalGames = allGames.size();

        List<UserGame> completedGames = userGameRepository.findAllWithMediaByUserIdAndStatus(user.getId(), MediaStatus.COMPLETED);
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
                    .sorted((a, b) -> {
                        int aMinutes = a.getPlaytimeMinutes() != null ? a.getPlaytimeMinutes() : 0;
                        int bMinutes = b.getPlaytimeMinutes() != null ? b.getPlaytimeMinutes() : 0;
                        return Integer.compare(bMinutes, aMinutes);
                    })
                    .toList();
            case HIGHEST_RATED -> games.stream()
                    .sorted((a, b) -> {
                        int aAchievements = a.getAchievementsUnlocked() != null ? a.getAchievementsUnlocked() : 0;
                        int bAchievements = b.getAchievementsUnlocked() != null ? b.getAchievementsUnlocked() : 0;
                        return Integer.compare(bAchievements, aAchievements);
                    })
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
        String thumbnail = media.getThumbnail();
        List<String> genres = userGame.getGenres();
        List<String> platforms = userGame.getPlatforms();
        List<StoreInfo> stores = rawgStoreService.applySteamFallback(
                userGame.getStores(),
                userGame.getSteamAppId()
        );

        return new UserGameResponse(
                userGame.getId(),
                media.getId(),
                name,
                thumbnail,
                userGame.getStatus(),
                userGame.isPlatinumed(),
                userGame.getPlaytimeMinutes(),
                userGame.getAchievementsUnlocked(),
                userGame.getTotalAchievements(),
                media.isHighlighted(),
                media.getDisplayOrder(),
                genres,
                platforms,
                stores
        );
    }
}
