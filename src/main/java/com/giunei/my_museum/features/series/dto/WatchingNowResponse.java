package com.giunei.my_museum.features.series.dto;

public record WatchingNowResponse(
        Long id,
        String title,
        String thumbnail,
        Integer pageCount,
        Integer currentSeason,
        Integer currentEpisode
) {
}
