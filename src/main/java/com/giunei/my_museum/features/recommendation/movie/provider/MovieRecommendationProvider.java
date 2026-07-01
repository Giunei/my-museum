package com.giunei.my_museum.features.recommendation.movie.provider;

import com.giunei.my_museum.features.movie.client.TmdbMovieClient;
import com.giunei.my_museum.features.movie.dto.MovieItem;
import com.giunei.my_museum.features.movie.dto.MovieResponse;
import com.giunei.my_museum.features.movie.mapper.MovieMapper;
import com.giunei.my_museum.features.recommendation.provider.RecommendationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieRecommendationProvider
        implements RecommendationProvider<MovieResponse> {

    private final TmdbMovieClient tmdbMovieClient;
    private final MovieMapper movieMapper;

    @Override
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
