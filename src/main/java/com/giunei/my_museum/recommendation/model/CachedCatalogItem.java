package com.giunei.my_museum.recommendation.model;

import com.giunei.my_museum.recommendation.entity.EditorialCategory;

import java.util.Set;

public record CachedCatalogItem(
        Long id,
        String title,
        String creator,
        EditorialCategory editorialCategory,
        Set<String> genres
) implements RecommendationCatalogItem {

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public EditorialCategory getEditorialCategory() {
        return editorialCategory;
    }

    @Override
    public Set<String> getGenres() {
        return genres;
    }
}
