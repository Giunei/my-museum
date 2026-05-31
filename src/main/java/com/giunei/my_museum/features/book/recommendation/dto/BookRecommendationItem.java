package com.giunei.my_museum.features.book.recommendation.dto;

import com.giunei.my_museum.features.book.catalog.entity.BookEditorialCategory;

import java.util.Set;

public record BookRecommendationItem(
        Long id,
        String title,
        String author,
        Set<String> genres,
        BookEditorialCategory editorialCategory,
        int score
) {
}
