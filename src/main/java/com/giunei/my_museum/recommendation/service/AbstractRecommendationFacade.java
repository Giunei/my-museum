package com.giunei.my_museum.recommendation.service;

import com.giunei.my_museum.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.dto.RecommendationSectionResponse;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public abstract class AbstractRecommendationFacade<T, R> {

    private static final ExecutorService RECOMMENDATION_FETCH_EXECUTOR = Executors.newFixedThreadPool(
            4,
            runnable -> {
                Thread thread = new Thread(runnable, "recommendation-fetch");
                thread.setDaemon(true);
                return thread;
            }
    );

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
        return buildCards(allItems, getTrendingCount());
    }

    protected List<R> buildCards(List<RecommendationItem> allItems, int limitPerBucket) {
        List<RecommendationItem> selected = selectItems(allItems, limitPerBucket);

        if (selected.size() <= 1) {
            return selected.stream()
                    .map(item -> toCard(item, fetchExternalData(item)))
                    .toList();
        }

        return selected.stream()
                .map(item -> CompletableFuture.supplyAsync(
                        () -> toCard(item, fetchExternalData(item)),
                        RECOMMENDATION_FETCH_EXECUTOR
                ))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .toList();
    }

    protected List<RecommendationItem> selectItems(List<RecommendationItem> allItems) {
        return selectItems(allItems, getTrendingCount(), getPopularCount(), getClassicCount(), getTotalRecommendations());
    }

    protected List<RecommendationItem> selectItems(List<RecommendationItem> allItems, int limit) {
        int safeLimit = Math.max(1, limit);
        // Mix editorial buckets, but never exceed the requested total size.
        int perBucket = Math.max(1, (safeLimit + 2) / 3);
        return selectItems(allItems, perBucket, perBucket, perBucket, safeLimit);
    }

    private List<RecommendationItem> selectItems(
            List<RecommendationItem> allItems,
            int trendingCount,
            int popularCount,
            int classicCount,
            int totalRecommendations
    ) {
        Map<EditorialCategory, List<RecommendationItem>> buckets = groupByCategory(allItems);

        List<RecommendationItem> selected = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        takeFromBucket(buckets.get(EditorialCategory.TRENDING), trendingCount, selected, seen, totalRecommendations);
        takeFromBucket(buckets.get(EditorialCategory.BESTSELLER), popularCount, selected, seen, totalRecommendations);
        takeFromBucket(buckets.get(EditorialCategory.CLASSIC), classicCount, selected, seen, totalRecommendations);

        if (selected.size() < totalRecommendations) {
            fillRemaining(buckets, selected, seen, totalRecommendations - selected.size());
        }

        return selected;
    }

    protected Set<Long> extractDisplayedIds(RecommendationResponse response) {
        return extractDisplayedIds(response, getTrendingCount());
    }

    protected Set<Long> extractDisplayedIds(RecommendationResponse response, int limitPerBucket) {
        return selectItems(flatten(response), limitPerBucket).stream()
                .map(RecommendationItem::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    protected List<RecommendationItem> flatten(RecommendationResponse response) {
        if (response == null || response.buckets() == null) {
            return List.of();
        }

        return response.buckets().stream()
                .flatMap(bucket -> bucket.items().stream())
                .toList();
    }

    protected <C> RecommendationSectionResponse<C> toSection(
            boolean available,
            int ratedCount,
            int minRatedCount,
            List<C> items
    ) {
        if (!available) {
            return RecommendationSectionResponse.unavailable(ratedCount, minRatedCount);
        }
        return RecommendationSectionResponse.available(ratedCount, minRatedCount, items);
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
                                Set<Long> seen,
                                int totalRecommendations) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (RecommendationItem item : items) {
            if (selected.size() >= totalRecommendations || limit == 0) {
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
