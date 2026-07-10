package com.giunei.my_museum.recommendation.dto;

import com.giunei.my_museum.recommendation.entity.EditorialCategory;

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
