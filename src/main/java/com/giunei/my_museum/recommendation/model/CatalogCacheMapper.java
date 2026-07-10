package com.giunei.my_museum.recommendation.model;

import com.giunei.my_museum.book.catalog.entity.BookCatalog;
import com.giunei.my_museum.game.entity.GameCatalog;
import com.giunei.my_museum.movie.catalog.entity.MovieCatalog;
import com.giunei.my_museum.series.catalog.entity.SeriesCatalog;

import java.util.Set;

public final class CatalogCacheMapper {

    private CatalogCacheMapper() {
    }

    public static CachedCatalogItem from(BookCatalog book) {
        return new CachedCatalogItem(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getEditorialCategory(),
                Set.copyOf(book.getGenres())
        );
    }

    public static CachedCatalogItem from(MovieCatalog movie) {
        return new CachedCatalogItem(
                movie.getId(),
                movie.getTitle(),
                movie.getCreator(),
                movie.getEditorialCategory(),
                Set.copyOf(movie.getGenres())
        );
    }

    public static CachedCatalogItem from(SeriesCatalog series) {
        return new CachedCatalogItem(
                series.getId(),
                series.getTitle(),
                series.getCreator(),
                series.getEditorialCategory(),
                Set.copyOf(series.getGenres())
        );
    }

    public static CachedCatalogItem from(GameCatalog game) {
        return new CachedCatalogItem(
                game.getId(),
                game.getTitle(),
                game.getCreator(),
                game.getEditorialCategory(),
                Set.copyOf(game.getGenres())
        );
    }
}
