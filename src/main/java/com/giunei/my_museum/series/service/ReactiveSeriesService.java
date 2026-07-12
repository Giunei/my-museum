package com.giunei.my_museum.series.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.UserCollectionInfo;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.series.client.TmdbSeriesClient;
import com.giunei.my_museum.series.dto.SeriesResponse;
import com.giunei.my_museum.series.dto.TmdbSeriesResponse;
import com.giunei.my_museum.series.mapper.SeriesMapper;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReactiveSeriesService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveSeriesService.class);

    private final TmdbSeriesClient client;
    private final SeriesMapper mapper;
    private final UserMediaRepository userMediaRepository;

    @Value("${tmdb.api.curated.concurrency:4}")
    private int curatedConcurrency;

    private static final List<String> CURATED = List.of(
            "The Boys",
            "Arcane",
            "Sugar",
            "One Piece: A Série",
            "Round 6",
            "Supernatural",
            "Friends",
            "The Bear",
            "Euphoria",
            "Bridgerton"
    );

    public Flux<SeriesResponse> getCuratedSeries() {
        User user = SecurityUtils.getAuthenticatedUserOrNull();

        return Flux.fromIterable(CURATED)
                .flatMap(term ->
                                Mono.fromCallable(() -> client.searchSeries(term, 1))
                                        .onErrorResume(e -> {
                                            log.warn("Failed to fetch curated series for term='{}': {}", term, e.toString());
                                            return Mono.empty();
                                        })
                                        .flatMapMany(response -> response.results() == null
                                                ? Flux.empty()
                                                : Flux.fromIterable(response.results()).take(1))
                                        .map(item -> {
                                            try {
                                                UserCollectionInfo collectionInfo = user != null
                                                        ? getUserCollectionInfo(user, item.id().longValue())
                                                        : UserCollectionInfo.notInCollection();
                                                return mapper.toResponse(item, collectionInfo);
                                            } catch (Exception e) {
                                                log.warn("Failed to map TMDB series item for term='{}': {}", term, e.toString());
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull),
                        curatedConcurrency
                )
                .onErrorContinue((e, obj) ->
                        log.warn("Ignoring stream error for obj='{}': {}", obj, e.toString()));
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

    public Mono<TmdbSeriesResponse> search(String query, int page) {
        int tmdbPage = page + 1;
        return Mono.fromCallable(() -> client.searchSeries(query, tmdbPage))
                .onErrorResume(e -> {
                    log.warn("Failed to search series for query='{}': {}", query, e.toString());
                    return Mono.empty();
                });
    }
}
