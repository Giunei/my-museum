package com.giunei.my_museum.features.achievement.dto;

import java.time.LocalDate;

public record UserGoalUpdateRequest(
        Integer target,
        LocalDate startDate,
        LocalDate endDate
) {
}
