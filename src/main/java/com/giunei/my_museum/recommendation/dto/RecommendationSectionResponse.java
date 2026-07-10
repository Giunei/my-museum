package com.giunei.my_museum.recommendation.dto;

import java.util.List;

public record RecommendationSectionResponse<T>(
        boolean available,
        int ratedCount,
        int minRatedCount,
        List<T> items
) {

    public static <T> RecommendationSectionResponse<T> unavailable(int ratedCount, int minRatedCount) {
        return new RecommendationSectionResponse<>(false, ratedCount, minRatedCount, List.of());
    }

    public static <T> RecommendationSectionResponse<T> available(
            int ratedCount,
            int minRatedCount,
            List<T> items
    ) {
        return new RecommendationSectionResponse<>(true, ratedCount, minRatedCount, items);
    }
}
