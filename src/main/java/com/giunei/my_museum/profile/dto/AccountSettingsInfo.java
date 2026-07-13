package com.giunei.my_museum.profile.dto;

import java.time.LocalDateTime;

public record AccountSettingsInfo(
        String email,
        boolean emailVerified,
        LocalDateTime nextUsernameChangeAvailableAt
) {
}
