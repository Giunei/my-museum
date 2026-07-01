package com.giunei.my_museum.features.user.profile.dto;

import java.time.LocalDate;

public record PersonInfo(
        String name,
        String nationality,
        String gender,
        LocalDate birthDate
) {
}
