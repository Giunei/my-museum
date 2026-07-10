package com.giunei.my_museum.series.dto;

public record WatchingNowResponse(
        Long id,
        String title,
        String thumbnail,
        Integer pageCount,
        Integer currentSeason,
        Integer currentEpisode
) {
}
