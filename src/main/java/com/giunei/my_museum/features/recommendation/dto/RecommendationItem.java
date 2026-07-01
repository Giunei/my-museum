package com.giunei.my_museum.features.recommendation.dto;

import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;

import java.util.Set;

public record RecommendationItem(
        Long id,
        String title,
        String creator,
        Set<String> genres,
        EditorialCategory editorialCategory,
        int score
) {
}
