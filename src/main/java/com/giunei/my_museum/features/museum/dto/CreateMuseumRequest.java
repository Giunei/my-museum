package com.giunei.my_museum.features.museum.dto;

import jakarta.validation.constraints.Positive;

public record CreateMuseumRequest(
        @Positive
        Long userId
) {
}
