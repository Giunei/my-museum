package com.giunei.my_museum.features.game.dto;

import com.giunei.my_museum.features.media.enums.MediaStatus;

import java.util.List;

public record UserGameResponse(
        Long id,
        Long mediaId,
        String name,
        String thumbnail,
        MediaStatus status,
        boolean platinumed,
        Integer playtimeMinutes,
        Integer achievementsUnlocked,
        Integer totalAchievements,
        boolean highlighted,
        Integer displayOrder,
        List<String> genres,
        List<String> platforms,
        List<StoreInfo> stores
) {
}
