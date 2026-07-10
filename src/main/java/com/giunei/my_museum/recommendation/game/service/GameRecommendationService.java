package com.giunei.my_museum.recommendation.game.service;

import com.giunei.my_museum.game.catalog.service.RecommendationGameCatalogService;
import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
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
import java.util.Set;

@Service
public class GameRecommendationService extends AbstractGenreRecommendationService<CachedCatalogItem> {

    private final RecommendationGameCatalogService gameCatalogService;

    public GameRecommendationService(
            PreferenceRepository preferenceRepository,
            UserMediaRepository userMediaRepository,
            RecommendationGameCatalogService gameCatalogService
    ) {
        super(preferenceRepository, userMediaRepository);
        this.gameCatalogService = gameCatalogService;
    }

    public RecommendationResponse recommendedForYou(User user, int limitPerBucket) {
        return recommendedForYou(user, PreferenceType.GAME, limitPerBucket, loadCatalog());
    }

    public MaybeYouLikeResult maybeYouLike(User user, int limitPerBucket, Set<Long> excludeIds) {
        return maybeYouLike(user, MediaType.GAME, PreferenceType.GAME, limitPerBucket, loadCatalog(), excludeIds);
    }

    public List<CachedCatalogItem> loadCatalog() {
        return gameCatalogService.findRecommendationCatalog();
    }
}
