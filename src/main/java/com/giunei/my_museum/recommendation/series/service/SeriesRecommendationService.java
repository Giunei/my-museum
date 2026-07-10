package com.giunei.my_museum.recommendation.series.service;

import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.recommendation.dto.MaybeYouLikeResult;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.service.AbstractGenreRecommendationService;
import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
import com.giunei.my_museum.series.catalog.service.SeriesCatalogService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.preference.entity.PreferenceType;
import com.giunei.my_museum.preference.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SeriesRecommendationService extends AbstractGenreRecommendationService<CachedCatalogItem> {

    private final SeriesCatalogService seriesCatalogService;

    public SeriesRecommendationService(
            PreferenceRepository preferenceRepository,
            UserMediaRepository userMediaRepository,
            SeriesCatalogService seriesCatalogService
    ) {
        super(preferenceRepository, userMediaRepository);
        this.seriesCatalogService = seriesCatalogService;
    }

    public RecommendationResponse recommendedForYou(User user, int limitPerBucket) {
        return recommendedForYou(user, PreferenceType.SERIES, limitPerBucket, loadCatalog());
    }

    public MaybeYouLikeResult maybeYouLike(User user, int limitPerBucket, Set<Long> excludeIds) {
        return maybeYouLike(user, MediaType.SERIES, PreferenceType.SERIES, limitPerBucket, loadCatalog(), excludeIds);
    }

    public List<CachedCatalogItem> loadCatalog() {
        return seriesCatalogService.findRecommendationCatalog();
    }
}
