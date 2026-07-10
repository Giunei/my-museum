package com.giunei.my_museum.achievement.dto;

import java.time.LocalDateTime;

public record AchievementResponse(
        String code,
        String name,
        String description,
        String imageUrl,
        LocalDateTime unlockedAt
) {}

