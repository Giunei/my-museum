package com.giunei.my_museum.home.dto;

public record PopularProfileResponse(
        Long userId,
        String username,
        String profileImageUrl,
        String name,
        String bio,
        long followersCount,
        long achievementsCount,
        long ratingsCount,
        int ranking
) {}
