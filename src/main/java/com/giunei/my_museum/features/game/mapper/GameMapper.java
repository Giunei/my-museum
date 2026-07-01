package com.giunei.my_museum.features.game.mapper;

import com.giunei.my_museum.features.game.dto.GameResponse;
import com.giunei.my_museum.features.game.dto.RawgGameResponse;
import com.giunei.my_museum.features.game.service.RawgStoreService;
import com.giunei.my_museum.features.media.dto.UserCollectionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameMapper {

    private final RawgStoreService rawgStoreService;

    public GameResponse toResponse(RawgGameResponse.RawgGameItem item) {
        return toResponse(item, null);
    }

    public GameResponse toResponse(RawgGameResponse.RawgGameItem item, UserCollectionInfo userCollectionInfo) {
        return new GameResponse(
                item.id() != null ? item.id().longValue() : null,
                item.name(),
                item.slug(),
                item.backgroundImage(),
                parseReleaseDate(item.releaseDate()),
                item.metacritic(),
                extractDeveloper(item.developers()),
                extractPublisher(item.publishers()),
                extractGenreNames(item.genres()),
                extractPlatformNames(item.platforms()),
                rawgStoreService.resolveStoreInfos(item, null),
                userCollectionInfo
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

    private LocalDate parseReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(releaseDate);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractDeveloper(List<RawgGameResponse.RawgDeveloper> developers) {
        if (developers == null || developers.isEmpty()) {
            return null;
        }
        return developers.get(0).name();
    }

    private String extractPublisher(List<RawgGameResponse.RawgPublisher> publishers) {
        if (publishers == null || publishers.isEmpty()) {
            return null;
        }
        return publishers.get(0).name();
    }
}
