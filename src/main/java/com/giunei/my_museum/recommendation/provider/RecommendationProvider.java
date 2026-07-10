package com.giunei.my_museum.recommendation.provider;

public interface RecommendationProvider<T> {

    T fetch(
            String title,
            String creator
    );
}
