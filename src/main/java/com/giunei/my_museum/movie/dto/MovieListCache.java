package com.giunei.my_museum.movie.dto;

import java.util.List;

public record MovieListCache(
        List<MovieResponse> movies
) {
}
