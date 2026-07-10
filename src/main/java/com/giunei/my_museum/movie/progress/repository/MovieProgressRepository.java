package com.giunei.my_museum.movie.progress.repository;

import com.giunei.my_museum.movie.progress.entity.MovieProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieProgressRepository extends JpaRepository<MovieProgress, Long> {
}
