package com.giunei.my_museum.movie.catalog.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.recommendation.model.RecommendationCatalogItem;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "movie_catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCatalog extends EntityAbstract implements RecommendationCatalogItem {

    @Column(nullable = false, unique = true)
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    @Column
    private String originalTitle;

    @Column
    private String overview;

    @Column
    private String posterPath;

    @Column
    private String backdropPath;

    @Column
    private LocalDate releaseDate;

    @Column
    private Double voteAverage;

    @Column
    private Integer voteCount;

    @Column
    private Double popularity;

    @Column(length = 10)
    private String originalLanguage;

    @Column
    private Boolean adult;

    @Column
    private Boolean video;

    @Column
    private String director;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EditorialCategory editorialCategory;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_catalog_genre", joinColumns = @JoinColumn(name = "movie_catalog_id"))
    @Column(name = "genre", nullable = false)
    @Builder.Default
    private Set<String> genres = new LinkedHashSet<>();

    @Override
    public String getCreator() {
        return director;
    }
}
