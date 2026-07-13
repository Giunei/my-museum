package com.giunei.my_museum.common.ratelimit;

import com.giunei.my_museum.common.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class AuthRateLimitService {

    private static final BucketConfiguration LOGIN_CONFIG = BucketConfiguration.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(5)
                    .refillGreedy(5, Duration.ofMinutes(1))
                    .build())
            .build();

    private static final BucketConfiguration REGISTER_CONFIG = BucketConfiguration.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(3)
                    .refillGreedy(3, Duration.ofHours(1))
                    .build())
            .build();

    private final ObjectProvider<ProxyManager<String>> proxyManager;
    private final ConcurrentHashMap<String, Bucket> localBuckets = new ConcurrentHashMap<>();

    public AuthRateLimitService(ObjectProvider<ProxyManager<String>> proxyManager) {
        this.proxyManager = proxyManager;
    }

    public void checkLogin(String clientIp) {
        consume("auth:login:" + clientIp, () -> LOGIN_CONFIG,
                "Muitas tentativas de login. Tente novamente em breve.");
    }

    public void checkRegister(String clientIp) {
        consume("auth:register:" + clientIp, () -> REGISTER_CONFIG,
                "Limite de cadastros atingido para este IP. Tente novamente mais tarde.");
    }

    private void consume(String key, Supplier<BucketConfiguration> config, String message) {
        ConsumptionProbe probe = resolveBucket(key, config).tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            long retryAfterSeconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
            throw new RateLimitExceededException(message, retryAfterSeconds);
        }
    }

    private Bucket resolveBucket(String key, Supplier<BucketConfiguration> config) {
        ProxyManager<String> distributed = proxyManager.getIfAvailable();
        if (distributed != null) {
            return distributed.getProxy(key, config);
        }

        return localBuckets.computeIfAbsent(key, ignored -> {
            BucketConfiguration configuration = config.get();
            var builder = Bucket.builder();
            for (Bandwidth bandwidth : configuration.getBandwidths()) {
                builder.addLimit(bandwidth);
            }
            return builder.build();
        });
    }
}
