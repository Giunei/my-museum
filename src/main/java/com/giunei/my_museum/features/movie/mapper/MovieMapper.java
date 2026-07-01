package com.giunei.my_museum.features.movie.mapper;

import com.giunei.my_museum.features.media.dto.UserCollectionInfo;
import com.giunei.my_museum.features.movie.dto.MovieItem;
import com.giunei.my_museum.features.movie.dto.MovieResponse;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String TMDB_BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/original";

    public MovieResponse toResponse(MovieItem item) {
        return toResponse(item, null);
    }

    public MovieResponse toResponse(MovieItem item, UserCollectionInfo userCollectionInfo) {
        if (item == null) return null;

        String thumbnail = item.posterPath() != null ? TMDB_IMAGE_BASE_URL + item.posterPath() : null;
        String backdropUrl = item.backdropPath() != null ? TMDB_BACKDROP_BASE_URL + item.backdropPath() : null;

        return new MovieResponse(
                item.id() != null ? item.id().longValue() : null,
                item.title(),
                item.originalTitle(),
                item.overview(),
                thumbnail,
                backdropUrl,
                item.releaseDate(),
                item.voteAverage(),
                item.voteCount(),
                item.popularity(),
                item.originalLanguage(),
                item.adult(),
                item.video(),
                userCollectionInfo
        );
    }
}
