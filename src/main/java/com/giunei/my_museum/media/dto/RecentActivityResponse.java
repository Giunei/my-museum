package com.giunei.my_museum.media.dto;

import com.giunei.my_museum.media.enums.MediaType;

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
