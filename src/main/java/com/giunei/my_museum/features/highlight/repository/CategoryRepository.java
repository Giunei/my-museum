package com.giunei.my_museum.features.highlight.repository;

import com.giunei.my_museum.features.highlight.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
