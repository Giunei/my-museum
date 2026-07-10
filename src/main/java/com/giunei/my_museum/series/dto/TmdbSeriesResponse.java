package com.giunei.my_museum.series.dto;

import java.util.List;

public record TmdbSeriesResponse(
        Integer page,
        Integer total_pages,
        Integer total_results,
        List<SeriesItem> results
) {
}
