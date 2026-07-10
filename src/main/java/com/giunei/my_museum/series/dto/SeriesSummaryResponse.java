package com.giunei.my_museum.series.dto;

import java.util.List;

public record SeriesSummaryResponse(
        int totalSeries,
        int seriesWatched,
        Integer totalEpisodesWatched,
        List<String> favoriteGenres
) {
}
