package com.giunei.my_museum.features.movie.dto;

import com.giunei.my_museum.features.media.dto.UserCollectionInfo;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String originalTitle,
        String overview,
        String thumbnail,
        String backdropUrl,
        LocalDate releaseDate,
        Double voteAverage,
        Integer voteCount,
        Double popularity,
        String originalLanguage,
        Boolean adult,
        Boolean video,
        UserCollectionInfo userCollectionInfo
) {
}
