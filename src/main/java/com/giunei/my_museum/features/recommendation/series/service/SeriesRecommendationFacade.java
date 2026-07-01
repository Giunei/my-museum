package com.giunei.my_museum.features.recommendation.series.service;

import com.giunei.my_museum.features.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.features.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.series.dto.SeriesRecommendationCardResponse;
import com.giunei.my_museum.features.recommendation.series.provider.SeriesRecommendationProvider;
import com.giunei.my_museum.features.recommendation.service.AbstractRecommendationFacade;
import com.giunei.my_museum.features.series.dto.SeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesRecommendationFacade extends AbstractRecommendationFacade<SeriesResponse, SeriesRecommendationCardResponse> {

    private static final int TOTAL_RECOMMENDATIONS = 10;
    private static final int TRENDING_COUNT = 4;
    private static final int POPULAR_COUNT = 3;
    private static final int CLASSIC_COUNT = 3;

    private final SeriesRecommendationService recommendationService;
    private final SeriesRecommendationProvider provider;

    public List<SeriesRecommendationCardResponse> recommendedForYou() {
        RecommendationResponse response = recommendationService.recommendedForYou(maxPerBucket());
        return buildCardsFromResponse(response);
    }

    private List<SeriesRecommendationCardResponse> buildCardsFromResponse(RecommendationResponse response) {
        List<RecommendationItem> allItems = response.buckets().stream()
                .flatMap(bucket -> bucket.items().stream())
                .toList();
        return buildCards(allItems);
    }

    @Override
    protected SeriesRecommendationCardResponse toCard(RecommendationItem item, SeriesResponse series) {
        return new SeriesRecommendationCardResponse(
                item.id(),
                item.title(),
                item.creator(),
                item.genres(),
                item.editorialCategory(),
                item.score(),
                series
        );
    }

    @Override
    protected SeriesResponse fetchExternalData(RecommendationItem item) {
        return provider.fetch(item.title(), item.creator());
    }

    @Override
    protected List<RecommendationItem> getBuckets() {
        return List.of();
    }

    @Override
    protected int getTotalRecommendations() {
        return TOTAL_RECOMMENDATIONS;
    }

    @Override
    protected int getTrendingCount() {
        return TRENDING_COUNT;
    }

    @Override
    protected int getPopularCount() {
        return POPULAR_COUNT;
    }

    @Override
    protected int getClassicCount() {
        return CLASSIC_COUNT;
    }

    private int maxPerBucket() {
        return Math.max(TRENDING_COUNT, Math.max(POPULAR_COUNT, CLASSIC_COUNT));
    }
}
