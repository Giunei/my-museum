package com.giunei.my_museum.features.user.profile.dto;

import com.giunei.my_museum.features.user.entity.Gender;
import com.giunei.my_museum.features.user.entity.Nationality;
import com.giunei.my_museum.features.user.profile.ProfileTheme;

import java.time.LocalDate;

public record CompleteProfileRequest(
        String name,
        Nationality nationality,
        Gender gender,
        LocalDate birthDate,
        ProfileTheme theme
) {
}
