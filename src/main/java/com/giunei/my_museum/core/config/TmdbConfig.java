package com.giunei.my_museum.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class TmdbConfig {

    @Bean
    public RestClient tmdbRestClient(@Value("${tmdb.api.token}") String token) {
        return RestClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                )
                .build();
    }
}