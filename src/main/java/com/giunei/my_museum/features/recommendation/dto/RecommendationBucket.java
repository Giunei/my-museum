package com.giunei.my_museum.features.recommendation.dto;

import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;

import java.util.List;

public record RecommendationBucket(
        EditorialCategory category,
        List<RecommendationItem> items
) {
}
