package com.giunei.my_museum.media.dto;

import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.enums.MediaStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UserMediaRequest(
        @NotBlank
        @Size(max = 255)
        String externalId,

        @NotNull
        MediaType type,

        @NotBlank
        @Size(max = 255)
        String title,

        @Size(max = 255)
        String thumbnail,

        Boolean completed,

        @Min(0)
        @Max(5)
        Integer rating,

        LocalDate finishedAt,

        @PositiveOrZero
        Integer pageCount,

        MediaStatus status,

        @PositiveOrZero
        Integer currentSeason,

        @PositiveOrZero
        Integer currentEpisode,

        @Size(max = 255)
        String author,

        List<Long> collectionIds
) {
}
