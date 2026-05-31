package com.giunei.my_museum.features.book.recommendation.dto;

import com.giunei.my_museum.features.book.catalog.entity.BookEditorialCategory;

import java.util.List;

public record BookRecommendationBucket(
        BookEditorialCategory category,
        List<BookRecommendationItem> books
) {
}
