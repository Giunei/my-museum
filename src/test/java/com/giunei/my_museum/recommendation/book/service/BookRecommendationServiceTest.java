package com.giunei.my_museum.recommendation.book.service;

import com.giunei.my_museum.book.catalog.service.BookCatalogService;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.preference.repository.PreferenceRepository;
import com.giunei.my_museum.recommendation.dto.MaybeYouLikeResult;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BookRecommendationServiceTest extends AbstractUnitTest {

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private UserMediaRepository userMediaRepository;

    @Mock
    private BookCatalogService bookCatalogService;

    @Mock
    private RatedBookBehaviorEnricher ratedBookBehaviorEnricher;

    @InjectMocks
    private BookRecommendationService bookRecommendationService;

    @Test
    void should_returnEmptyBuckets_when_userHasNoBookPreferences() {
        var user = TestFixtures.user(1L, "testuser");

        when(preferenceRepository.findByUser(user)).thenReturn(List.of());
        when(bookCatalogService.findRecommendationCatalog()).thenReturn(List.of(TestFixtures.catalogItem("Duna")));

        RecommendationResponse response = bookRecommendationService.recommendedForYou(user, 4);

        assertThat(response.buckets()).hasSize(3);
        assertThat(response.buckets()).allMatch(bucket -> bucket.items().isEmpty());
    }

    @Test
    void should_returnUnavailable_when_userHasFewRatedBooks() {
        var user = TestFixtures.user(1L, "testuser");

        when(userMediaRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(bookCatalogService.findRecommendationCatalog())
                .thenReturn(List.of(TestFixtures.catalogItem("Duna")));

        MaybeYouLikeResult result = bookRecommendationService.maybeYouLike(user, 4, Set.of());

        assertThat(result.available()).isFalse();
        assertThat(result.ratedCount()).isZero();
        assertThat(result.minRatedCountRequired()).isEqualTo(3);
    }
}
