package com.giunei.my_museum.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamOwnedGamesResponse(
        Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            Integer game_count,
            List<Game> games
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Game(
            Integer appid,
            String name,
            Integer playtime_forever,
            String img_icon_url,
            Boolean has_community_visible_stats
    ) {
    }
}
