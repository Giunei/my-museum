package com.giunei.my_museum.features.lol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RiotSummonerResponse(
        String puuid,
        @JsonProperty("profileIconId") Integer profileIconId,
        @JsonProperty("summonerLevel") Long summonerLevel
) {
}
