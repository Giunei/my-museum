package com.giunei.my_museum.features.recommendation.book.service;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.recommendation.book.dto.BookRecommendationCardResponse;
import com.giunei.my_museum.features.recommendation.book.provider.BookRecommendationProvider;
import com.giunei.my_museum.features.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.features.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.service.AbstractRecommendationFacade;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookRecommendationFacade extends AbstractRecommendationFacade<BookResponse, BookRecommendationCardResponse> {

    private static final int TOTAL_RECOMMENDATIONS = 10;
    private static final int TRENDING_COUNT = 4;
    private static final int POPULAR_COUNT = 3;
    private static final int CLASSIC_COUNT = 3;

    private final BookRecommendationService recommendationService;
    private final BookRecommendationProvider provider;

    public List<BookRecommendationCardResponse> recommendedForYou(User user) {
        RecommendationResponse response = recommendationService.recommendedForYou(user, maxPerBucket());
        return buildCardsFromResponse(response);
    }

    public List<BookRecommendationCardResponse> maybeYouLike(User user) {
        RecommendationResponse response = recommendationService.maybeYouLike(user, maxPerBucket());
        return buildCardsFromResponse(response);
    }

    private List<BookRecommendationCardResponse> buildCardsFromResponse(RecommendationResponse response) {
        List<RecommendationItem> allItems = response.buckets().stream()
                .flatMap(bucket -> bucket.items().stream())
                .toList();
        return buildCards(allItems);
    }

    @Override
    protected BookRecommendationCardResponse toCard(RecommendationItem item, BookResponse book) {
        return new BookRecommendationCardResponse(
                item.id(),
                item.title(),
                item.creator(),
                item.genres(),
                item.editorialCategory(),
                item.score(),
                book
        );
    }

    @Override
    protected BookResponse fetchExternalData(RecommendationItem item) {
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
