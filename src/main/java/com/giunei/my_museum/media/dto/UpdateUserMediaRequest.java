package com.giunei.my_museum.media.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

public record UpdateUserMediaRequest(
        @Min(0)
        @Max(5)
        Integer rating,

        LocalDate finishedAt,

        Boolean highlighted,

        @PositiveOrZero
        Integer currentSeason,

        @PositiveOrZero
        Integer currentEpisode,

        List<Long> collectionIds
) {
}
