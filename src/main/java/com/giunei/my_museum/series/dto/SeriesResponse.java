package com.giunei.my_museum.series.dto;

import com.giunei.my_museum.media.dto.UserCollectionInfo;

import java.time.LocalDate;
import java.util.List;

public record SeriesResponse(
        Long id,
        String name,
        String originalName,
        String overview,
        String thumbnail,
        String backdropUrl,
        LocalDate firstAirDate,
        Double voteAverage,
        Integer voteCount,
        Double popularity,
        String originalLanguage,
        List<String> originCountry,
        UserCollectionInfo userCollectionInfo
) {
}
