package com.giunei.my_museum.features.media.dto;

import java.time.LocalDate;

public record UpdateUserMediaRequest(
        Integer rating,
        LocalDate finishedAt,
        Boolean highlighted
) {
}
