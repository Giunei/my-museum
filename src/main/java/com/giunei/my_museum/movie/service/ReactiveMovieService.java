package com.giunei.my_museum.movie.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.UserCollectionInfo;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.movie.client.TmdbMovieClient;
import com.giunei.my_museum.movie.dto.MovieResponse;
import com.giunei.my_museum.movie.dto.TmdbMovieResponse;
import com.giunei.my_museum.movie.mapper.MovieMapper;
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
public class ReactiveMovieService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveMovieService.class);

    private final TmdbMovieClient client;
    private final MovieMapper mapper;
    private final UserMediaRepository userMediaRepository;

    @Value("${tmdb.api.curated.concurrency:4}")
    private int curatedConcurrency;

    private static final List<String> CURATED = List.of(
            "Moana",
            "Supergirl",
            "HOMEM-ARANHA: UM NOVO DIA",
            "Duna: Parte Dois",
            "Oppenheimer",
            "Barbie",
            "Divertidamente 2",
            "Deadpool & Wolverine",
            "Michael",
            "O Diabo Veste Prada 2"
    );

    public Flux<MovieResponse> getCuratedMovies() {
        User user = SecurityUtils.getAuthenticatedUserOrNull();

        return Flux.fromIterable(CURATED)
                .flatMap(term ->
                                Mono.fromCallable(() -> client.searchMovies(term, 1))
                                        .onErrorResume(e -> {
                                            log.warn("Failed to fetch curated movie for term='{}': {}", term, e.toString());
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
                                                log.warn("Failed to map TMDB movie item for term='{}': {}", term, e.toString());
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

    public Mono<TmdbMovieResponse> search(String query, int page) {
        int tmdbPage = page + 1;
        return Mono.fromCallable(() -> client.searchMovies(query, tmdbPage))
                .doOnError(e -> log.error("Error searching movies for query='{}': {}", query, e.getMessage()))
                .onErrorResume(e -> {
                    log.error("Failed to search movies for query='{}': {}", query, e.toString());
                    return Mono.empty();
                });
    }
}
