package com.giunei.my_museum.common.ratelimit;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@ConditionalOnBean(RedisConnectionFactory.class)
public class RateLimitRedisConfig {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "app.rate-limit", name = "redis-enabled", havingValue = "true", matchIfMissing = true)
    RedisClient rateLimitRedisClient(
            @Value("${spring.data.redis.url:}") String redisUrl,
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port,
            @Value("${spring.data.redis.username:}") String username,
            @Value("${spring.data.redis.password:}") String password,
            @Value("${spring.data.redis.ssl.enabled:false}") boolean ssl
    ) {
        return RedisClient.create(buildRedisUri(redisUrl, host, port, username, password, ssl));
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnBean(name = "rateLimitRedisClient")
    StatefulRedisConnection<String, byte[]> rateLimitRedisConnection(
            @Qualifier("rateLimitRedisClient") RedisClient rateLimitRedisClient
    ) {
        return rateLimitRedisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    @ConditionalOnBean(name = "rateLimitRedisConnection")
    ProxyManager<String> rateLimitProxyManager(
            @Qualifier("rateLimitRedisConnection") StatefulRedisConnection<String, byte[]> connection
    ) {
        return LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(2))
                )
                .build();
    }

    private static RedisURI buildRedisUri(
            String redisUrl,
            String host,
            int port,
            String username,
            String password,
            boolean ssl
    ) {
        if (StringUtils.hasText(redisUrl)) {
            return RedisURI.create(redisUrl);
        }

        RedisURI.Builder builder = RedisURI.builder()
                .withHost(StringUtils.hasText(host) ? host : "localhost")
                .withPort(port);

        if (ssl) {
            builder.withSsl(true);
        }

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            builder.withAuthentication(username, password.toCharArray());
        } else if (StringUtils.hasText(password)) {
            builder.withPassword(password.toCharArray());
        }

        return builder.build();
    }
}
