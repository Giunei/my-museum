package com.giunei.my_museum.movie.client;

import com.giunei.my_museum.movie.dto.TmdbMovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TmdbMovieClient {

    private final RestClient tmdbRestClient;

    public TmdbMovieResponse searchMovies(String query, int page) {
        return tmdbRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("language", "pt-BR")
                        .build()
                )
                .retrieve()
                .body(TmdbMovieResponse.class);
    }
}
