package com.giunei.my_museum.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecommendationCacheConfig {

    @Bean(CacheManagers.RECOMMENDATION)
    public CacheManager recommendationCacheManager() {
        return new ConcurrentMapCacheManager(
                "book-recommendation-catalog",
                "movie-recommendation-catalog",
                "series-recommendation-catalog",
                "game-recommendation-catalog",
                "books:recommendation-detail",
                "books:volume-snapshot",
                "movies:recommendation-detail",
                "series:recommendation-detail",
                "games:recommendation-detail",
                "games:search"
        );
    }
}
