package com.giunei.my_museum.features.user.preference.dto;

import java.util.List;

public record PreferenceOptionsResponse(
        List<PreferenceOption> bookGenres,
        List<PreferenceOption> movieGenres,
        List<PreferenceOption> seriesGenres,
        List<PreferenceOption> gameGenres
) {
}
