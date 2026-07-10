package com.giunei.my_museum.recommendation.dto;

public record MaybeYouLikeResult(
        boolean available,
        int ratedCount,
        int minRatedCountRequired,
        RecommendationResponse response
) {

    public static MaybeYouLikeResult unavailable(int ratedCount, int minRequired) {
        return new MaybeYouLikeResult(false, ratedCount, minRequired, RecommendationResponse.empty());
    }

    public static MaybeYouLikeResult available(int ratedCount, int minRequired, RecommendationResponse response) {
        return new MaybeYouLikeResult(true, ratedCount, minRequired, response);
    }
}
