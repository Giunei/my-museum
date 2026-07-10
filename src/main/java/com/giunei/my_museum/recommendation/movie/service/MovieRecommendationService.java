package com.giunei.my_museum.recommendation.movie.service;

import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
import com.giunei.my_museum.movie.catalog.service.MovieCatalogService;
import com.giunei.my_museum.recommendation.dto.MaybeYouLikeResult;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.service.AbstractGenreRecommendationService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.preference.entity.PreferenceType;
import com.giunei.my_museum.preference.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MovieRecommendationService extends AbstractGenreRecommendationService<CachedCatalogItem> {

    private final MovieCatalogService movieCatalogService;

    public MovieRecommendationService(
            PreferenceRepository preferenceRepository,
            UserMediaRepository userMediaRepository,
            MovieCatalogService movieCatalogService
    ) {
        super(preferenceRepository, userMediaRepository);
        this.movieCatalogService = movieCatalogService;
    }

    public RecommendationResponse recommendedForYou(User user, int limitPerBucket) {
        return recommendedForYou(user, PreferenceType.MOVIE, limitPerBucket, loadCatalog());
    }

    public MaybeYouLikeResult maybeYouLike(User user, int limitPerBucket, Set<Long> excludeIds) {
        return maybeYouLike(user, MediaType.MOVIE, PreferenceType.MOVIE, limitPerBucket, loadCatalog(), excludeIds);
    }

    public List<CachedCatalogItem> loadCatalog() {
        return movieCatalogService.findRecommendationCatalog();
    }
}
