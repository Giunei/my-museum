package com.giunei.my_museum.features.game.client;

import com.giunei.my_museum.features.game.dto.RawgGameStoresResponse;
import com.giunei.my_museum.features.game.dto.RawgGameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RawgClient {

    private final RestClient rawgRestClient;

    @Value("${rawg.api.key}")
    private String apiKey;

    public RawgGameResponse searchGames(String query, int page) {
        return rawgRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", apiKey)
                        .queryParam("search", query)
                        .queryParam("page", page)
                        .queryParam("page_size", 1)
                        .build())
                .retrieve()
                .body(RawgGameResponse.class);
    }

    public RawgGameStoresResponse getGameStores(long gameId) {
        return rawgRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{id}/stores")
                        .queryParam("key", apiKey)
                        .queryParam("page_size", 40)
                        .build(gameId))
                .retrieve()
                .body(RawgGameStoresResponse.class);
    }
}
