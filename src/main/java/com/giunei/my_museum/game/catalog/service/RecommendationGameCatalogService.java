package com.giunei.my_museum.game.catalog.service;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.game.repository.GameCatalogRepository;
import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
import com.giunei.my_museum.recommendation.model.CatalogCacheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationGameCatalogService {

    private final GameCatalogRepository repository;

    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "game-recommendation-catalog")
    @Transactional(readOnly = true)
    public List<CachedCatalogItem> findRecommendationCatalog() {
        return repository.findByRawgIdBetween(990_001L, 999_999L).stream()
                .map(CatalogCacheMapper::from)
                .toList();
    }
}
