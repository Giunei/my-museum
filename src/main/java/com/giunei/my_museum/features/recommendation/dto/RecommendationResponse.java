package com.giunei.my_museum.features.recommendation.dto;

import java.util.List;

public record RecommendationResponse(
        List<RecommendationBucket> buckets
) {
}
