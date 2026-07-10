package com.giunei.my_museum.integration.lol.dto;

import com.giunei.my_museum.integration.lol.enums.LolPlatform;

public record LolConnectionStatusResponse(
        boolean connected,
        String gameName,
        String tagLine,
        LolPlatform platform
) {
    public static LolConnectionStatusResponse disconnected() {
        return new LolConnectionStatusResponse(false, null, null, null);
    }
}
