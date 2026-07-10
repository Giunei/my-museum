package com.giunei.my_museum.profile.dto;

import com.giunei.my_museum.profile.ProfileTheme;

public record ProfileInfo(
        String profileImageUrl,
        String description,
        String bio,
        ProfileTheme theme
) {
}
