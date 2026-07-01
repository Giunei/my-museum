package com.giunei.my_museum.features.lol.dto;

import com.giunei.my_museum.features.lol.enums.LolPlatform;

import java.time.LocalDateTime;

public record LolRankResponse(
        String gameName,
        String tagLine,
        LolPlatform platform,
        Integer summonerLevel,
        LolQueueRankResponse soloDuo,
        LolQueueRankResponse flex,
        LocalDateTime lastRefreshAt
) {
}
