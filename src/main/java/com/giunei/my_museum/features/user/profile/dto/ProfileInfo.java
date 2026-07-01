package com.giunei.my_museum.features.user.profile.dto;

import com.giunei.my_museum.features.user.profile.ProfileTheme;

public record ProfileInfo(
        String profileImageUrl,
        String bio,
        ProfileTheme theme
) {
}
