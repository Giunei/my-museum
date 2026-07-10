package com.giunei.my_museum.achievement.dto;

import com.giunei.my_museum.achievement.entity.GoalType;
import com.giunei.my_museum.media.enums.MediaType;

import java.time.LocalDate;

public record UserGoalResponse(
        Long id,
        MediaType type,
        GoalType goalType,
        Integer target,
        Integer progress,
        LocalDate startDate,
        LocalDate endDate,
        boolean completed
) {
}
