package com.giunei.my_museum.media.dto;

import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.enums.MediaStatus;

import java.time.LocalDate;
import java.util.List;

public record UserMediaResponse(
        Long id,
        String externalId,
        MediaType type,
        String title,
        String thumbnail,
        Boolean completed,
        Integer rating,
        LocalDate finishedAt,
        MediaStatus status,
        Integer currentSeason,
        Integer currentEpisode,
        String author,
        List<Long> collectionIds,
        Boolean highlighted
) {}