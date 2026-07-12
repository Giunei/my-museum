package com.giunei.my_museum.game.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.game.client.RawgClient;
import com.giunei.my_museum.game.dto.GameResponse;
import com.giunei.my_museum.game.dto.RawgGameResponse;
import com.giunei.my_museum.game.mapper.GameMapper;
import com.giunei.my_museum.media.dto.UserCollectionInfo;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveGameService {

    private final RawgClient rawgClient;
    private final GameMapper gameMapper;
    private final UserMediaRepository userMediaRepository;

    @Value("${rawg.api.curated.concurrency:4}")
    private int curatedConcurrency;

    private static final List<String> CURATED = List.of(
            "Terraria",
            "Assassin's Creed Black Flag Resynced",
            "007 First Light",
            "Elden Ring",
            "Crimson Desert",
            "Marvel Rivals",
            "Stardew Valley",
            "Split Fiction",
            "Clair Obscur: Expedition 33",
            "Palworld"
    );

    public Flux<GameResponse> searchGames(String query, int page) {
        return Mono.fromCallable(() -> rawgClient.searchGames(query, page))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch games for query='{}': {}", query, e.toString());
                    return Mono.empty();
                })
                .flatMapMany(response -> response == null || response.results() == null
                        ? Flux.empty()
                        : Flux.fromIterable(response.results()))
                .map(item -> {
                    try {
                        return gameMapper.toSearchResponse(item);
                    } catch (Exception e) {
                        log.warn("Failed to map RAWG game item for query='{}': {}", query, e.toString());
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    public Flux<GameResponse> getCuratedGames() {
        User user = tryGetAuthenticatedUser();

        return Flux.fromIterable(CURATED)
                .flatMap(term -> fetchBestMatch(term)
                                .map(item -> {
                                    try {
                                        UserCollectionInfo collectionInfo = user != null
                                                ? getUserCollectionInfo(user, item.id().longValue())
                                                : UserCollectionInfo.notInCollection();
                                        return gameMapper.toSearchResponse(item, collectionInfo);
                                    } catch (Exception e) {
                                        log.warn("Failed to map RAWG game item for term='{}': {}", term, e.toString());
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull),
                        curatedConcurrency
                )
                .onErrorContinue((e, obj) ->
                        log.warn("Ignoring stream error for obj='{}': {}", obj, e.toString()));
    }

    private Mono<RawgGameResponse.RawgGameItem> fetchBestMatch(String term) {
        return Mono.fromCallable(() -> rawgClient.searchGames(term, 1, 1))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch curated game for term='{}': {}", term, e.toString());
                    return Mono.empty();
                })
                .flatMap(response -> {
                    if (response == null || response.results() == null || response.results().isEmpty()) {
                        log.warn("No results found for term='{}'", term);
                        return Mono.empty();
                    }
                    return Mono.just(response.results().get(0));
                });
    }

    private User tryGetAuthenticatedUser() {
        try {
            return SecurityUtils.getAuthenticatedUser();
        } catch (Exception e) {
            return null;
        }
    }

    private UserCollectionInfo getUserCollectionInfo(User user, Long externalId) {
        return userMediaRepository.findByUserAndExternalId(user, String.valueOf(externalId))
                .map(media -> new UserCollectionInfo(
                        true,
                        media.getStatus(),
                        media.getRating(),
                        media.getFinishedAt(),
                        media.getCurrentSeason(),
                        media.getCurrentEpisode()
                ))
                .orElse(UserCollectionInfo.notInCollection());
    }
}
