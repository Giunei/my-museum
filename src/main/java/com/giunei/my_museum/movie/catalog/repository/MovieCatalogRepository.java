package com.giunei.my_museum.movie.catalog.repository;

import com.giunei.my_museum.movie.catalog.entity.MovieCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCatalogRepository extends JpaRepository<MovieCatalog, Long> {
}
