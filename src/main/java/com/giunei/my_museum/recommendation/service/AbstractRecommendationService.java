package com.giunei.my_museum.recommendation.service;

import com.giunei.my_museum.recommendation.dto.RecommendationBucket;
import com.giunei.my_museum.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.recommendation.model.RecommendationCatalogItem;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

    protected RecommendationResponse bucketize(
            List<RecommendationItem> items,
            int trendingLimit,
            int bestsellerLimit,
            int classicLimit
    ) {
        Map<EditorialCategory, List<RecommendationItem>> buckets = new EnumMap<>(EditorialCategory.class);

        buckets.put(EditorialCategory.TRENDING, filterByCategory(items, EditorialCategory.TRENDING, trendingLimit));
        buckets.put(EditorialCategory.BESTSELLER, filterByCategory(items, EditorialCategory.BESTSELLER, bestsellerLimit));
        buckets.put(EditorialCategory.CLASSIC, filterByCategory(items, EditorialCategory.CLASSIC, classicLimit));

        return new RecommendationResponse(List.of(
                new RecommendationBucket(EditorialCategory.TRENDING, buckets.get(EditorialCategory.TRENDING)),
                new RecommendationBucket(EditorialCategory.BESTSELLER, buckets.get(EditorialCategory.BESTSELLER)),
                new RecommendationBucket(EditorialCategory.CLASSIC, buckets.get(EditorialCategory.CLASSIC))
        ));
    }

    protected List<RecommendationItem> filterByCategory(
            List<RecommendationItem> items,
            EditorialCategory category,
            int limit
    ) {
        return items.stream()
                .filter(item -> item.editorialCategory() == category)
                .limit(limit)
                .toList();
    }
}
