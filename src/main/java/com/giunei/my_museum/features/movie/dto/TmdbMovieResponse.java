package com.giunei.my_museum.features.movie.dto;

import java.util.List;

public record TmdbMovieResponse(
        Integer page,
        Integer total_pages,
        Integer total_results,
        List<MovieItem> results
) {
}
