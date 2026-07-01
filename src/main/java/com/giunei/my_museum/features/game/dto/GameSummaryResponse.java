package com.giunei.my_museum.features.game.dto;

import java.util.List;

public record GameSummaryResponse(
    int totalGames,
    int completedGames,
    double totalPlaytimeHours,
    List<String> favoriteGenres
) {}
