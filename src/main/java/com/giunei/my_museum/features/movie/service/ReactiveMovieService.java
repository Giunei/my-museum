package com.giunei.my_museum.features.movie.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.media.dto.UserCollectionInfo;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.movie.client.TmdbMovieClient;
import com.giunei.my_museum.features.movie.dto.MovieItem;
import com.giunei.my_museum.features.movie.dto.TmdbMovieResponse;
import com.giunei.my_museum.features.movie.dto.MovieResponse;
import com.giunei.my_museum.features.movie.mapper.MovieMapper;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

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
            "O Poderoso Chefão",
            "Pulp Fiction",
            "O Senhor dos Anéis",
            "Matrix",
            "Interestelar",
            "O Resgate do Soldado Ryan",
            "Forrest Gump",
            "O Rei Leão",
            "Gladiador",
            "Coração Valente"
    );

    public Flux<MovieResponse> getCuratedMovies() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();
            return Flux.fromIterable(CURATED)
                    .flatMap(term ->
                                    Mono.fromCallable(() -> client.searchMovies(term, 1))
                                            .onErrorResume(e -> {
                                                log.warn("Failed to fetch curated movie for term='{}': {}", term, e.toString());
                                                return Mono.empty();
                                            })
                                            .flatMapMany(response -> response.results() == null
                                                    ? Flux.empty()
                                                    : Flux.fromIterable(response.results()))
                                            .map(item -> {
                                                try {
                                                    UserCollectionInfo collectionInfo = getUserCollectionInfo(user, item.id().longValue());
                                                    return mapper.toResponse(item, collectionInfo);
                                                } catch (Exception e) {
                                                    log.warn("Failed to map TMDB movie item for term='{}': {}", term, e.toString());
                                                    return null;
                                                }
                                            })
                                            .filter(Objects::nonNull),
                            curatedConcurrency
                    )
                    .collectList()
                    .flatMapMany(movies -> {
                        // Remove sequels/duplicates by keeping only the first movie of each base title
                        // Base title is the title without sequel indicators (II, III, 2, 3, etc.)
                        Set<String> seenBaseTitles = new HashSet<>();
                        List<MovieResponse> filtered = new ArrayList<>();

                        for (MovieResponse movie : movies) {
                            String baseTitle = extractBaseTitle(movie.title());
                            if (!seenBaseTitles.contains(baseTitle)) {
                                seenBaseTitles.add(baseTitle);
                                filtered.add(movie);
                            }
                        }

                        return Flux.fromIterable(filtered);
                    })
                    .take(20)
                    .onErrorContinue((e, obj) ->
                            log.warn("Ignoring stream error for obj='{}': {}", obj, e.toString()));
        } catch (Exception e) {
            // User not authenticated, return without collection info
            return Flux.fromIterable(CURATED)
                    .flatMap(term ->
                                    Mono.fromCallable(() -> client.searchMovies(term, 1))
                                            .onErrorResume(e2 -> {
                                                log.warn("Failed to fetch curated movie for term='{}': {}", term, e2.toString());
                                                return Mono.empty();
                                            })
                                            .flatMapMany(response -> response.results() == null
                                                    ? Flux.empty()
                                                    : Flux.fromIterable(response.results()))
                                            .map(item -> {
                                                try {
                                                    return mapper.toResponse(item, UserCollectionInfo.notInCollection());
                                                } catch (Exception e2) {
                                                    log.warn("Failed to map TMDB movie item for term='{}': {}", term, e2.toString());
                                                    return null;
                                                }
                                            })
                                            .filter(Objects::nonNull),
                            curatedConcurrency
                    )
                    .collectList()
                    .flatMapMany(movies -> {
                        // Remove sequels/duplicates by keeping only the first movie of each base title
                        // Base title is the title without sequel indicators (II, III, 2, 3, etc.)
                        Set<String> seenBaseTitles = new HashSet<>();
                        List<MovieResponse> filtered = new ArrayList<>();

                        for (MovieResponse movie : movies) {
                            String baseTitle = extractBaseTitle(movie.title());
                            if (!seenBaseTitles.contains(baseTitle)) {
                                seenBaseTitles.add(baseTitle);
                                filtered.add(movie);
                            }
                        }

                        return Flux.fromIterable(filtered);
                    })
                    .take(20)
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

    private String extractBaseTitle(String title) {
        if (title == null) {
            return "";
        }
        // Remove sequel indicators: II, III, IV, V, VI, VII, VIII, IX, X, 2, 3, 4, 5, 6, 7, 8, 9, 10
        String baseTitle = title
                .replaceAll("(?i)\\s+(II|III|IV|V|VI|VII|VIII|IX|X|2|3|4|5|6|7|8|9|10)\\s*$", "")
                .replaceAll("(?i)\\s+Part\\s+\\d+\\s*$", "")
                .replaceAll("(?i)\\s+-\\s+.*$", "")
                .trim();
        return baseTitle;
    }

    @Cacheable(value = "movies:search",
            key = "#query + '-' + #page")
    public Mono<TmdbMovieResponse> search(String query, int page) {
        return Mono.fromCallable(() -> client.searchMovies(query, page))
                .onErrorResume(e -> {
                    log.warn("Failed to search movies for query='{}': {}", query, e.toString());
                    return Mono.empty();
                });
    }
}
