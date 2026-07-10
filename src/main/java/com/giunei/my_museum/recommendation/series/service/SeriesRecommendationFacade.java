package com.giunei.my_museum.recommendation.series.service;

import com.giunei.my_museum.recommendation.dto.MaybeYouLikeResult;
import com.giunei.my_museum.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.dto.RecommendationSectionResponse;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.recommendation.series.dto.SeriesRecommendationCardResponse;
import com.giunei.my_museum.recommendation.series.provider.SeriesRecommendationProvider;
import com.giunei.my_museum.recommendation.service.AbstractRecommendationFacade;
import com.giunei.my_museum.series.dto.SeriesResponse;
import com.giunei.my_museum.user.entity.User;
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

    public List<SeriesRecommendationCardResponse> recommendedForYou(User user, int limitPerBucket) {
        RecommendationResponse response = recommendationService.recommendedForYou(user, limitPerBucket);
        return buildCardsFromResponse(response, limitPerBucket);
    }

    public RecommendationSectionResponse<SeriesRecommendationCardResponse> maybeYouLike(User user, int limitPerBucket) {
        RecommendationResponse forYou = recommendationService.recommendedForYou(user, limitPerBucket);
        MaybeYouLikeResult discovery = recommendationService.maybeYouLike(
                user,
                limitPerBucket,
                extractDisplayedIds(forYou, limitPerBucket)
        );

        if (!discovery.available()) {
            return toSection(false, discovery.ratedCount(), discovery.minRatedCountRequired(), List.of());
        }

        List<SeriesRecommendationCardResponse> cards = buildCards(flatten(discovery.response()), limitPerBucket);
        return toSection(true, discovery.ratedCount(), discovery.minRatedCountRequired(), cards);
    }

    private List<SeriesRecommendationCardResponse> buildCardsFromResponse(
            RecommendationResponse response,
            int limitPerBucket
    ) {
        return buildCards(flatten(response), limitPerBucket);
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
}
