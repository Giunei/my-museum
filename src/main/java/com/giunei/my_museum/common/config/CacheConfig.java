package com.giunei.my_museum.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
@Profile("!test")
@ConditionalOnBean(RedisConnectionFactory.class)
public class CacheConfig {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);
    private static final Duration BOOKS_SEARCH_TTL = Duration.ofHours(12);
    private static final Duration BOOKS_CURATED_TTL = Duration.ofHours(24);

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return baseCacheConfiguration(DEFAULT_TTL);
    }

    @Bean
    @Primary
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaults = cacheConfiguration();

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        perCache.put("books:search", baseCacheConfiguration(BOOKS_SEARCH_TTL));
        perCache.put("books:curated", baseCacheConfiguration(BOOKS_CURATED_TTL));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(perCache)
                .transactionAware()
                .build();
    }

    private static RedisCacheConfiguration baseCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
    }
}
