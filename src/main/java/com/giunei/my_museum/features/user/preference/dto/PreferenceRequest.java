package com.giunei.my_museum.features.user.preference.dto;

import com.giunei.my_museum.features.user.preference.entity.BookGenre;
import com.giunei.my_museum.features.user.preference.entity.GameGenre;
import com.giunei.my_museum.features.user.preference.entity.MovieGenre;

import java.util.List;

public record PreferenceRequest(
        List<BookGenre> bookGenres,
        List<MovieGenre> movieGenres,
        List<MovieGenre> seriesGenres,
        List<GameGenre> gameGenres
) {
}
