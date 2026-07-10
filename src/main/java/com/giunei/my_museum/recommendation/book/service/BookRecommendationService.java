package com.giunei.my_museum.recommendation.book.service;

import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
import com.giunei.my_museum.book.catalog.service.BookCatalogService;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.recommendation.dto.MaybeYouLikeResult;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.service.AbstractGenreRecommendationService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.preference.entity.PreferenceType;
import com.giunei.my_museum.preference.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BookRecommendationService extends AbstractGenreRecommendationService<CachedCatalogItem> {

    private final BookCatalogService bookCatalogService;
    private final RatedBookBehaviorEnricher ratedBookBehaviorEnricher;

    public BookRecommendationService(
            PreferenceRepository preferenceRepository,
            UserMediaRepository userMediaRepository,
            BookCatalogService bookCatalogService,
            RatedBookBehaviorEnricher ratedBookBehaviorEnricher
    ) {
        super(preferenceRepository, userMediaRepository);
        this.bookCatalogService = bookCatalogService;
        this.ratedBookBehaviorEnricher = ratedBookBehaviorEnricher;
    }

    public RecommendationResponse recommendedForYou(User user, int limitPerBucket) {
        return recommendedForYou(user, PreferenceType.BOOK, limitPerBucket, loadCatalog());
    }

    public MaybeYouLikeResult maybeYouLike(User user, int limitPerBucket, Set<Long> excludeIds) {
        return maybeYouLike(user, MediaType.BOOK, PreferenceType.BOOK, limitPerBucket, loadCatalog(), excludeIds);
    }

    public List<CachedCatalogItem> loadCatalog() {
        return bookCatalogService.findRecommendationCatalog();
    }

    @Override
    protected void enrichBehaviorFromRatedMedia(
            UserMedia media,
            MediaType mediaType,
            int creatorWeight,
            int genreWeight,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights
    ) {
        if (mediaType != MediaType.BOOK) {
            return;
        }
        ratedBookBehaviorEnricher.enrich(media, creatorWeight, genreWeight, creatorWeights, genreWeights);
    }
}
