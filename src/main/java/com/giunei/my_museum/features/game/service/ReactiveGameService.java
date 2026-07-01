package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.game.client.RawgClient;
import com.giunei.my_museum.features.game.dto.GameResponse;
import com.giunei.my_museum.features.game.dto.RawgGameResponse;
import com.giunei.my_museum.features.game.mapper.GameMapper;
import com.giunei.my_museum.features.media.dto.UserCollectionInfo;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
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
            "Lies of P",
            "007 First Light",
            "Cuphead",
            "Crimson Desert",
            "Marvel Rivals"
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
                        return gameMapper.toResponse(item);
                    } catch (Exception e) {
                        log.warn("Failed to map RAWG game item for query='{}': {}", query, e.toString());
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    public Flux<GameResponse> getCuratedGames() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();
            log.info("Fetching curated games with terms: {}", CURATED);
            return Flux.fromIterable(CURATED)
                    .flatMap(term ->
                            Mono.fromCallable(() -> {
                                log.info("Searching for game with term: {}", term);
                                return rawgClient.searchGames(term, 1);
                            })
                                    .onErrorResume(e -> {
                                        log.warn("Failed to fetch curated game for term='{}': {}", term, e.toString());
                                        return Mono.empty();
                                    })
                                    .flatMapMany(response -> {
                                        if (response == null || response.results() == null || response.results().isEmpty()) {
                                            log.warn("No results found for term='{}'", term);
                                            return Flux.empty();
                                        }
                                        log.info("Found {} results for term='{}'", response.results().size(), term);
                                        return Flux.fromIterable(response.results());
                                    })
                                    .map(item -> {
                                        try {
                                            UserCollectionInfo collectionInfo = getUserCollectionInfo(user, item.id().longValue());
                                            return gameMapper.toResponse(item, collectionInfo);
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
        } catch (Exception e) {
            // User not authenticated, return without collection info
            log.info("Fetching curated games with terms: {}", CURATED);
            return Flux.fromIterable(CURATED)
                    .flatMap(term ->
                            Mono.fromCallable(() -> {
                                log.info("Searching for game with term: {}", term);
                                return rawgClient.searchGames(term, 1);
                            })
                                    .onErrorResume(e2 -> {
                                        log.warn("Failed to fetch curated game for term='{}': {}", term, e2.toString());
                                        return Mono.empty();
                                    })
                                    .flatMapMany(response -> {
                                        if (response == null || response.results() == null || response.results().isEmpty()) {
                                            log.warn("No results found for term='{}'", term);
                                            return Flux.empty();
                                        }
                                        log.info("Found {} results for term='{}'", response.results().size(), term);
                                        return Flux.fromIterable(response.results());
                                    })
                                    .map(item -> {
                                        try {
                                            return gameMapper.toResponse(item, UserCollectionInfo.notInCollection());
                                        } catch (Exception e2) {
                                            log.warn("Failed to map RAWG game item for term='{}': {}", term, e2.toString());
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull),
                            curatedConcurrency
                    )
                    .onErrorContinue((e2, obj) ->
                            log.warn("Ignoring stream error for obj='{}': {}", obj, e2.toString()));
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
