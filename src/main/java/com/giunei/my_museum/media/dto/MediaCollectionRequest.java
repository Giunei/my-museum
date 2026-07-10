package com.giunei.my_museum.media.dto;

import com.giunei.my_museum.media.enums.MediaType;

public record MediaCollectionRequest(
        MediaType type,
        String name,
        String icon
) {
}
