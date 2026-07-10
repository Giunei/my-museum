package com.giunei.my_museum.game.dto;

import com.giunei.my_museum.game.entity.GameGenre;

import java.util.List;
import java.util.Set;

public record SteamSummaryResponse(
        int gamesInLibrary,
        int completedGames,
        int platinumedGames,
        double totalHoursPlayed,
        int totalAchievements,
        Set<GameGenre> favoriteGenres
) {
}
