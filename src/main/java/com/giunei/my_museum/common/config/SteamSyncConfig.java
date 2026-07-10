package com.giunei.my_museum.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SteamSyncConfig {

    private static final int ENRICHMENT_THREAD_POOL_SIZE = 8;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService steamSyncEnrichmentExecutor() {
        return Executors.newFixedThreadPool(ENRICHMENT_THREAD_POOL_SIZE);
    }
}
