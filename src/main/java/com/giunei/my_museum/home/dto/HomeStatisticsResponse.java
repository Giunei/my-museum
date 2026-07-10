package com.giunei.my_museum.home.dto;

public record HomeStatisticsResponse(
        long totalAchievementsUnlocked,
        long totalRatedItems,
        long totalUsers
) {}
