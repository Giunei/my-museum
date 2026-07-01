package com.giunei.my_museum.features.recommendation.movie.dto;

import com.giunei.my_museum.features.movie.dto.MovieResponse;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;

import java.util.Set;

public record MovieRecommendationCardResponse(
        Long catalogId,
        String title,
        String director,
        Set<String> genres,
        EditorialCategory editorialCategory,
        int score,
        MovieResponse movie
) {
}
