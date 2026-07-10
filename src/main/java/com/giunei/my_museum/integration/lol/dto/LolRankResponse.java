package com.giunei.my_museum.integration.lol.dto;

import com.giunei.my_museum.integration.lol.enums.LolPlatform;

import java.time.LocalDateTime;

public record LolRankResponse(
        String gameName,
        String tagLine,
        LolPlatform platform,
        LolQueueRankResponse soloDuo,
        LolQueueRankResponse flex,
        LocalDateTime lastRefreshAt
) {
}
