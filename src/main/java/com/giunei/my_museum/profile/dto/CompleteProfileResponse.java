package com.giunei.my_museum.profile.dto;

import com.giunei.my_museum.user.entity.Gender;
import com.giunei.my_museum.user.entity.Nationality;

import java.time.LocalDateTime;

public record CompleteProfileResponse(
        String name,
        Nationality nationality,
        Gender gender,
        String username,
        String email,
        boolean emailVerified,
        boolean emailVerificationSent,
        LocalDateTime nextUsernameChangeAvailableAt,
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
