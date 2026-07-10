package com.giunei.my_museum.game.dto;

import com.giunei.my_museum.media.enums.MediaStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record UpdateUserGameRequest(
        @Min(0)
        @Max(5)
        Integer rating,

        LocalDate finishedAt,

        Boolean highlighted,

        MediaStatus status,

        Boolean platinumed
) {
}
