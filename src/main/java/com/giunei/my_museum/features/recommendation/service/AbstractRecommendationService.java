package com.giunei.my_museum.features.recommendation.service;

import com.giunei.my_museum.features.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.model.RecommendationCatalogItem;

import java.util.Comparator;

public abstract class AbstractRecommendationService<T extends RecommendationCatalogItem> {

    protected RecommendationItem toItem(T item, int score) {
        return new RecommendationItem(
                item.getId(),
                item.getTitle(),
                item.getCreator(),
                item.getGenres(),
                item.getEditorialCategory(),
                score
        );
    }

    protected Comparator<RecommendationItem> recommendationComparator() {
        return Comparator
                .comparingInt(RecommendationItem::score)
                .reversed()
                .thenComparing(
                        item -> editorialSortWeight(
                                item.editorialCategory()
                        )
                )
                .thenComparing(
                        RecommendationItem::title,
                        String.CASE_INSENSITIVE_ORDER
                );
    }

    protected int editorialWeight(EditorialCategory category) {
        return switch (category) {
            case TRENDING -> 40;
            case BESTSELLER -> 25;
            case CLASSIC -> 15;
        };
    }

    protected int editorialSortWeight(EditorialCategory category) {
        return switch (category) {
            case TRENDING -> 0;
            case BESTSELLER -> 1;
            case CLASSIC -> 2;
        };
    }
}
