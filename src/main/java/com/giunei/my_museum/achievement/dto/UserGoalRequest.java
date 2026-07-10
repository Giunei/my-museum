package com.giunei.my_museum.achievement.dto;

import com.giunei.my_museum.achievement.entity.GoalType;
import com.giunei.my_museum.media.enums.MediaType;

import java.time.LocalDate;

public record UserGoalRequest(
        MediaType type,
        GoalType goalType,
        Integer target,
        LocalDate startDate,
        LocalDate endDate
) {
}
