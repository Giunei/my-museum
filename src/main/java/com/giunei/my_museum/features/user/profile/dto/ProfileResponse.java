package com.giunei.my_museum.features.user.profile.dto;

public record ProfileResponse(
        Long id,
        String name,
        String profileImageUrl,
        int followers,
        String description,
        String nationality,
        String gender
) {
}
