package com.giunei.my_museum.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawgGameStoresResponse(
        List<GameStoreLink> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GameStoreLink(
            Integer id,
            String url,
            @JsonProperty("store_id") String storeId
    ) {
    }
}
