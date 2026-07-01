package com.giunei.my_museum.features.lol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RiotAccountResponse(
        String puuid,
        @JsonProperty("gameName") String gameName,
        @JsonProperty("tagLine") String tagLine
) {
}
