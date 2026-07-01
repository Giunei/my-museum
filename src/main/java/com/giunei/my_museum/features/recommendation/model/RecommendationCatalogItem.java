package com.giunei.my_museum.features.recommendation.model;

import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;

import java.util.Set;

public interface RecommendationCatalogItem {

    Long getId();

    String getTitle();

    Set<String> getGenres();

    EditorialCategory getEditorialCategory();

    String getCreator();
}
