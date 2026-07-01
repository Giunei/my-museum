package com.giunei.my_museum.features.recommendation.provider;

public interface RecommendationProvider<T> {

    T fetch(
            String title,
            String creator
    );
}
