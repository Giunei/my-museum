package com.giunei.my_museum.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamAchievementsResponse(
        PlayerStats playerstats
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PlayerStats(
            String steamID,
            String gameName,
            Integer achievements_unlocked,
            List<Achievement> achievements
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Achievement(
            String apiname,
            Integer achieved,
            String name,
            String description
    ) {
    }
}
