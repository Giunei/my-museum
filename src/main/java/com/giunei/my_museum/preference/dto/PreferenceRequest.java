package com.giunei.my_museum.preference.dto;

import com.giunei.my_museum.game.entity.GameGenre;
import com.giunei.my_museum.preference.entity.BookGenre;
import com.giunei.my_museum.preference.entity.MovieGenre;

import java.util.List;

public record PreferenceRequest(
        List<BookGenre> bookGenres,
        List<MovieGenre> movieGenres,
        List<MovieGenre> seriesGenres,
        List<GameGenre> gameGenres
) {
}
