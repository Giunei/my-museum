package com.giunei.my_museum.features.game.dto;

public record SteamConnectionStatusResponse(
        boolean connected,
        String steamId64,
        String personaName,
        String avatarUrl
) {
}
