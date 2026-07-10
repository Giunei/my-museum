package com.giunei.my_museum.recommendation.movie.provider;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.movie.client.TmdbMovieClient;
import com.giunei.my_museum.movie.dto.MovieItem;
import com.giunei.my_museum.movie.dto.MovieResponse;
import com.giunei.my_museum.movie.mapper.MovieMapper;
import com.giunei.my_museum.recommendation.provider.RecommendationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieRecommendationProvider
        implements RecommendationProvider<MovieResponse> {

    private final TmdbMovieClient tmdbMovieClient;
    private final MovieMapper movieMapper;

    @Override
    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "movies:recommendation-detail", key = "#title + '::' + #creator", unless = "#result == null")
    public MovieResponse fetch(String title, String creator) {
        try {
            var response = tmdbMovieClient.searchMovies(title, 1);
            if (response == null || response.results() == null || response.results().isEmpty()) {
                return null;
            }
            return movieMapper.toResponse(response.results().get(0));
        } catch (Exception ignored) {
            return null;
        }
    }
}
