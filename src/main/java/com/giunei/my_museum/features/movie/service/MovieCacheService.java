package com.giunei.my_museum.features.movie.service;

import com.giunei.my_museum.features.movie.dto.TmdbMovieResponse;
import com.giunei.my_museum.features.movie.dto.MovieListCache;
import com.giunei.my_museum.features.movie.dto.MovieResponse;
import com.giunei.my_museum.features.movie.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MovieCacheService {

    private static final Logger log = LoggerFactory.getLogger(MovieCacheService.class);

    private final ReactiveMovieService reactiveService;
    private final MovieMapper mapper;

    @Cacheable(value = "movies:search",
            key = "#query + '-' + #page")
    public List<MovieResponse> search(String query, int page) {
        return reactiveService.search(query, page)
                .map(response -> {
                    if (response.results() == null) {
                        return Collections.<MovieResponse>emptyList();
                    }
                    return response.results().stream()
                            .map(mapper::toResponse)
                            .filter(Objects::nonNull)
                            .toList();
                })
                .block();
    }

    @Cacheable(value = "movies:curated")
    public MovieListCache getCuratedMovies() {
        List<MovieResponse> movies = reactiveService.getCuratedMovies()
                .collectList()
                .block();

        return new MovieListCache(movies != null ? movies : Collections.emptyList());
    }
}
