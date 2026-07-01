package com.giunei.my_museum.features.movie.progress.repository;

import com.giunei.my_museum.features.movie.progress.entity.MovieProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieProgressRepository extends JpaRepository<MovieProgress, Long> {
}
