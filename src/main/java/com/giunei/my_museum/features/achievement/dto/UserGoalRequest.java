package com.giunei.my_museum.features.achievement.dto;

import com.giunei.my_museum.features.achievement.entity.GoalType;
import com.giunei.my_museum.features.media.enums.MediaType;

import java.time.LocalDate;

public record UserGoalRequest(
        MediaType type,
        GoalType goalType,
        Integer target,
        LocalDate startDate,
        LocalDate endDate
) {
}
