package com.giunei.my_museum.features.series.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record SeasonDetailResponse(
        @JsonProperty("season_number") Integer seasonNumber,
        String name,
        @JsonProperty("episode_count") Integer episodeCount,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("overview") String overview,
        @JsonProperty("air_date") LocalDate airDate,
        @JsonProperty("episodes") List<Episode> episodes
) {
    public record Episode(
            @JsonProperty("episode_number") Integer episodeNumber,
            String name,
            String overview,
            @JsonProperty("air_date") LocalDate airDate,
            @JsonProperty("still_path") String stillPath,
            @JsonProperty("vote_average") Double voteAverage,
            @JsonProperty("vote_count") Integer voteCount,
            @JsonProperty("runtime") Integer runtime
    ) {}
}
