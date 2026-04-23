package com.giunei.my_museum.features.media.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record UpdateUserMediaRequest(
        @Min(0)
        @Max(5)
        Integer rating,

        LocalDate finishedAt,

        Boolean highlighted
) {
}
