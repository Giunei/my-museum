package com.giunei.my_museum.features.media.dto;

import com.giunei.my_museum.features.media.enums.MediaType;

import java.time.LocalDate;

public record RecentActivityResponse(
        Long id,
        String title,
        String thumbnail,
        MediaType type,
        LocalDate finishedAt,
        Integer rating,
        String timeAgo
) {
}
