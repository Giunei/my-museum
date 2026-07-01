package com.giunei.my_museum.features.media.dto;

import com.giunei.my_museum.features.media.enums.MediaType;

public record MediaCollectionRequest(
        MediaType type,
        String name,
        String icon
) {
}
