package com.giunei.my_museum.features.series.progress.repository;

import com.giunei.my_museum.features.series.progress.entity.SeriesProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesProgressRepository extends JpaRepository<SeriesProgress, Long> {
}
