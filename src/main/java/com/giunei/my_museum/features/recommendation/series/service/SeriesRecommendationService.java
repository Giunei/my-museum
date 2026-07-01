package com.giunei.my_museum.features.recommendation.series.service;

import com.giunei.my_museum.features.recommendation.dto.RecommendationBucket;
import com.giunei.my_museum.features.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.features.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.model.RecommendationCatalogItem;
import com.giunei.my_museum.features.recommendation.service.AbstractRecommendationService;
import com.giunei.my_museum.features.series.catalog.entity.SeriesCatalog;
import com.giunei.my_museum.features.series.catalog.service.SeriesCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesRecommendationService extends AbstractRecommendationService<SeriesCatalog> {

    private final SeriesCatalogService seriesCatalogService;

    public RecommendationResponse recommendedForYou(int limitPerBucket) {
        List<SeriesCatalog> catalog = loadCatalog();
        List<RecommendationItem> items = catalog.stream()
                .limit(100)
                .map(book -> toItem(book, editorialWeight(book.getEditorialCategory())))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();

        return bucketize(items, limitPerBucket, limitPerBucket, limitPerBucket * 2);
    }

    public List<SeriesCatalog> loadCatalog() {
        return seriesCatalogService.findAll();
    }

    private RecommendationResponse bucketize(List<RecommendationItem> items,
                                                 int trendingLimit,
                                                 int bestsellerLimit,
                                                 int classicLimit) {
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

    private List<RecommendationItem> filterByCategory(List<RecommendationItem> items,
                                                      EditorialCategory category,
                                                      int limit) {
        return items.stream()
                .filter(item -> item.editorialCategory() == category)
                .limit(limit)
                .toList();
    }
}
