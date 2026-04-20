package com.giunei.my_museum.features.media.dto;

import com.giunei.my_museum.features.media.enums.MediaType;

import java.time.LocalDate;

public record UserMediaRequest(
        String externalId,
        MediaType type,
        String title,
        String thumbnail,
        Boolean completed,
        Integer rating,
        LocalDate finishedAt
) {}