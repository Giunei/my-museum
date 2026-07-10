package com.giunei.my_museum.series.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record SeriesDetailResponse(
        Integer id,
        String name,
        @JsonProperty("original_name") String originalName,
        String overview,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("backdrop_path") String backdropPath,
        @JsonProperty("first_air_date") LocalDate firstAirDate,
        @JsonProperty("vote_average") Double voteAverage,
        @JsonProperty("vote_count") Integer voteCount,
        Double popularity,
        @JsonProperty("original_language") String originalLanguage,
        @JsonProperty("genre_ids") List<Integer> genreIds,
        @JsonProperty("origin_country") List<String> originCountry,
        @JsonProperty("number_of_seasons") Integer numberOfSeasons,
        @JsonProperty("number_of_episodes") Integer numberOfEpisodes,
        @JsonProperty("seasons") List<Season> seasons
) {
    public record Season(
            @JsonProperty("season_number") Integer seasonNumber,
            String name,
            @JsonProperty("episode_count") Integer episodeCount,
            @JsonProperty("poster_path") String posterPath
    ) {}
}
