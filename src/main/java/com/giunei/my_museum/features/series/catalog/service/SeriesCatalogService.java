package com.giunei.my_museum.features.series.catalog.service;

import com.giunei.my_museum.features.series.catalog.entity.SeriesCatalog;
import com.giunei.my_museum.features.series.catalog.repository.SeriesCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesCatalogService {

    private final SeriesCatalogRepository repository;

    @Cacheable("series-catalog")
    public List<SeriesCatalog> findAll() {
        return repository.findAll();
    }
}
