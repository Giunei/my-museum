package com.giunei.my_museum.movie.dto;

import java.util.List;

public record MovieSummaryResponse(
        int totalMovies,
        int moviesWatched,
        List<String> favoriteGenres
) {
}
