package com.giunei.my_museum.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webConfig() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }
}
