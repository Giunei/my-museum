package com.giunei.my_museum.recommendation.series.dto;

import com.giunei.my_museum.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.series.dto.SeriesResponse;

import java.util.Set;

public record SeriesRecommendationCardResponse(
        Long catalogId,
        String name,
        String creator,
        Set<String> genres,
        EditorialCategory editorialCategory,
        int score,
        SeriesResponse series
) {
}
