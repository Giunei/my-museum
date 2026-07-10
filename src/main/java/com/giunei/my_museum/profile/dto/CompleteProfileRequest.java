package com.giunei.my_museum.profile.dto;

import com.giunei.my_museum.user.entity.Gender;
import com.giunei.my_museum.user.entity.Nationality;

import java.time.LocalDate;

public record CompleteProfileRequest(
        String name,
        Nationality nationality,
        Gender gender,
        LocalDate birthDate,
        String bio
) {
}
