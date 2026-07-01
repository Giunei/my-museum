package com.giunei.my_museum.features.series.mapper;

import com.giunei.my_museum.features.media.dto.UserCollectionInfo;
import com.giunei.my_museum.features.series.dto.SeriesItem;
import com.giunei.my_museum.features.series.dto.SeriesResponse;
import org.springframework.stereotype.Component;

@Component
public class SeriesMapper {

    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String TMDB_BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/original";

    public SeriesResponse toResponse(SeriesItem item) {
        return toResponse(item, null);
    }

    public SeriesResponse toResponse(SeriesItem item, UserCollectionInfo userCollectionInfo) {
        if (item == null) return null;

        String thumbnail = item.posterPath() != null ? TMDB_IMAGE_BASE_URL + item.posterPath() : null;
        String backdropUrl = item.backdropPath() != null ? TMDB_BACKDROP_BASE_URL + item.backdropPath() : null;

        return new SeriesResponse(
                item.id() != null ? item.id().longValue() : null,
                item.name(),
                item.originalName(),
                item.overview(),
                thumbnail,
                backdropUrl,
                item.firstAirDate(),
                item.voteAverage(),
                item.voteCount(),
                item.popularity(),
                item.originalLanguage(),
                item.originCountry(),
                userCollectionInfo
        );
    }
}
