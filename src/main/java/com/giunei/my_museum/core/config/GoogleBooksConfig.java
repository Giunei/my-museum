package com.giunei.my_museum.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleBooksConfig {

    @Bean
    public RestTemplate googleBooksRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient googleBooksWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }
}
