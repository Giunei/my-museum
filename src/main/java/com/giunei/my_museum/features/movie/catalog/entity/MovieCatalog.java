package com.giunei.my_museum.features.movie.catalog.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.model.RecommendationCatalogItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "movie_catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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

    @Override
    public String getCreator() {
        return director;
    }

    @Override
    public Set<String> getGenres() {
        return Set.of();
    }
}
