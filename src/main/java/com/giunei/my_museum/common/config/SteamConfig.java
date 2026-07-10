package com.giunei.my_museum.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SteamConfig {

    @Bean
    public WebClient steamWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.steampowered.com")
                .build();
    }
}
