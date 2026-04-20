package com.giunei.my_museum.features.user.dto;

public record UserReponse(
        Long id,
        String username,
        String email,
        boolean emailVerified,
        boolean onboardingCompleted
) {
}
