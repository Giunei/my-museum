package com.giunei.my_museum.features.user.preference.dto;

import java.util.List;

public record PreferenceResponse(
        List<String> bookGenres,
        List<String> movieGenres,
        List<String> seriesGenres,
        List<String> gameGenres
) {
}
