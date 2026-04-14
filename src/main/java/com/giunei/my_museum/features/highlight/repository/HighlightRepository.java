package com.giunei.my_museum.features.highlight.repository;

import com.giunei.my_museum.features.highlight.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {
}
