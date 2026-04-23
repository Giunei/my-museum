package com.giunei.my_museum.features.media.dto;

import com.giunei.my_museum.features.media.enums.MediaType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

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
        Integer pageCount
) {
}
