package com.giunei.my_museum.profile.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateProfilePrivacyRequest(
        @NotNull
        Boolean privateProfile
) {
}
