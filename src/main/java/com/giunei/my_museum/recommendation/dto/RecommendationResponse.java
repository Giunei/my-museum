package com.giunei.my_museum.recommendation.dto;

import java.util.List;

public record RecommendationResponse(
        List<RecommendationBucket> buckets
) {

    public static RecommendationResponse empty() {
        return new RecommendationResponse(List.of());
    }
}
