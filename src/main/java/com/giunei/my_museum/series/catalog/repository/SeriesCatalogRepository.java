package com.giunei.my_museum.series.catalog.repository;

import com.giunei.my_museum.series.catalog.entity.SeriesCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesCatalogRepository extends JpaRepository<SeriesCatalog, Long> {
}
