package com.giunei.my_museum.features.game.dto;

public record SteamSyncStatusResponse(
        boolean syncing,
        int current,
        int total,
        String message
) {
}
