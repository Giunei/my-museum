package com.giunei.my_museum.game.dto;

public record SteamSyncStatusResponse(
        boolean syncing,
        int current,
        int total,
        String message
) {
}
