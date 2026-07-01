package com.giunei.my_museum.features.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record MovieItem(
        Integer id,
        String title,
        @JsonProperty("original_title") String originalTitle,
        String overview,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("backdrop_path") String backdropPath,
        @JsonProperty("release_date") LocalDate releaseDate,
        @JsonProperty("vote_average") Double voteAverage,
        @JsonProperty("vote_count") Integer voteCount,
        Double popularity,
        @JsonProperty("original_language") String originalLanguage,
        Boolean adult,
        Boolean video,
        @JsonProperty("genre_ids") List<Integer> genreIds
) {
}
