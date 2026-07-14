package com.giunei.my_museum.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamPlayerSummaryResponse(
        Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            List<Player> players
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Player(
            String steamid,
            String personaname,
            String profileurl,
            String avatar,
            String avatarmedium,
            String avatarfull,
            /**
             * 1 = Private, 2 = Friends only, 3 = Public
             */
            Integer communityvisibilitystate
    ) {
    }
}
