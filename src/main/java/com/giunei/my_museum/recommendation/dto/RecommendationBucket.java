package com.giunei.my_museum.recommendation.dto;

import com.giunei.my_museum.recommendation.entity.EditorialCategory;

import java.util.List;

public record RecommendationBucket(
        EditorialCategory category,
        List<RecommendationItem> items
) {
}
