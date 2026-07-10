package com.giunei.my_museum.book.catalog.service;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.book.catalog.repository.BookCatalogRepository;
import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
import com.giunei.my_museum.recommendation.model.CatalogCacheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCatalogService {

    private final BookCatalogRepository repository;

    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "book-recommendation-catalog")
    @Transactional(readOnly = true)
    public List<CachedCatalogItem> findRecommendationCatalog() {
        return repository.findAll().stream()
                .map(CatalogCacheMapper::from)
                .toList();
    }
}
