package com.giunei.my_museum.recommendation.book.dto;

import com.giunei.my_museum.book.dto.BookResponse;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;

import java.util.Set;

public record BookRecommendationCardResponse(
        Long catalogId,
        String title,
        String author,
        Set<String> genres,
        EditorialCategory editorialCategory,
        int score,
        BookResponse book
) {
}
