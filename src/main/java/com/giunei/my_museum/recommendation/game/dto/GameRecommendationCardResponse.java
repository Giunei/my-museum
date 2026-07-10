package com.giunei.my_museum.recommendation.game.dto;

import com.giunei.my_museum.game.dto.GameResponse;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;

import java.util.Set;

public record GameRecommendationCardResponse(
        Long catalogId,
        String title,
        Set<String> genres,
        EditorialCategory editorialCategory,
        int score,
        GameResponse game
) {
}
