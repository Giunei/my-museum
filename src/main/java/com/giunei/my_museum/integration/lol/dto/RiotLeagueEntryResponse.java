package com.giunei.my_museum.integration.lol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RiotLeagueEntryResponse(
        @JsonProperty("queueType") String queueType,
        String tier,
        String rank,
        @JsonProperty("leaguePoints") Integer leaguePoints,
        Integer wins,
        Integer losses
) {
}
