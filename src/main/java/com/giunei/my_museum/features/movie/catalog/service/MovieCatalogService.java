package com.giunei.my_museum.features.movie.catalog.service;

import com.giunei.my_museum.features.movie.catalog.entity.MovieCatalog;
import com.giunei.my_museum.features.movie.catalog.repository.MovieCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieCatalogService {

    private final MovieCatalogRepository repository;

    @Cacheable("movie-catalog")
    public List<MovieCatalog> findAll() {
        return repository.findAll();
    }
}
