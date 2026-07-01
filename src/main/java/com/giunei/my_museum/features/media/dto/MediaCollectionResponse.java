package com.giunei.my_museum.features.media.dto;

import com.giunei.my_museum.features.media.enums.MediaType;

import java.time.LocalDateTime;

public record MediaCollectionResponse(
        Long id,
        MediaType type,
        String name,
        String icon,
        LocalDateTime createdAt
) {
}
