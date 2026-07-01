package com.giunei.my_museum.features.recommendation.service;

import com.giunei.my_museum.features.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractRecommendationFacade<T, R> {

    protected abstract R toCard(RecommendationItem item, T externalData);

    protected abstract T fetchExternalData(RecommendationItem item);

    protected abstract List<RecommendationItem> getBuckets();

    protected abstract int getTotalRecommendations();

    protected abstract int getTrendingCount();

    protected abstract int getPopularCount();

    protected abstract int getClassicCount();

    protected List<R> buildCards() {
        List<RecommendationItem> allItems = getBuckets();
        return buildCards(allItems);
    }

    protected List<R> buildCards(List<RecommendationItem> allItems) {
        Map<EditorialCategory, List<RecommendationItem>> buckets = groupByCategory(allItems);

        List<RecommendationItem> selected = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        takeFromBucket(buckets.get(EditorialCategory.TRENDING), getTrendingCount(), selected, seen);
        takeFromBucket(buckets.get(EditorialCategory.BESTSELLER), getPopularCount(), selected, seen);
        takeFromBucket(buckets.get(EditorialCategory.CLASSIC), getClassicCount(), selected, seen);

        if (selected.size() < getTotalRecommendations()) {
            fillRemaining(buckets, selected, seen, getTotalRecommendations() - selected.size());
        }

        return selected.stream()
                .map(item -> toCard(item, fetchExternalData(item)))
                .toList();
    }

    private Map<EditorialCategory, List<RecommendationItem>> groupByCategory(List<RecommendationItem> items) {
        Map<EditorialCategory, List<RecommendationItem>> buckets = new EnumMap<>(EditorialCategory.class);
        for (RecommendationItem item : items) {
            buckets.computeIfAbsent(item.editorialCategory(), k -> new ArrayList<>()).add(item);
        }
        return buckets;
    }

    private void takeFromBucket(List<RecommendationItem> items,
                                int limit,
                                List<RecommendationItem> selected,
                                Set<Long> seen) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (RecommendationItem item : items) {
            if (selected.size() >= getTotalRecommendations() || limit == 0) {
                break;
            }
            if (item.id() != null && seen.add(item.id())) {
                selected.add(item);
                limit--;
            }
        }
    }

    private void fillRemaining(Map<EditorialCategory, List<RecommendationItem>> buckets,
                               List<RecommendationItem> selected,
                               Set<Long> seen,
                               int remaining) {
        List<EditorialCategory> order = List.of(
                EditorialCategory.TRENDING,
                EditorialCategory.BESTSELLER,
                EditorialCategory.CLASSIC
        );

        for (EditorialCategory category : order) {
            List<RecommendationItem> items = buckets.get(category);
            if (items == null) {
                continue;
            }

            for (RecommendationItem item : items) {
                if (remaining <= 0) {
                    return;
                }
                if (item.id() != null && seen.add(item.id())) {
                    selected.add(item);
                    remaining--;
                }
            }
        }
    }
}
