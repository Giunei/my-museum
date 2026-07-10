package com.giunei.my_museum.user.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        boolean emailVerified,
        boolean onboardingCompleted
) {
}
