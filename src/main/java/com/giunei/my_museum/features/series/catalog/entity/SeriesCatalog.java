package com.giunei.my_museum.features.series.catalog.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.model.RecommendationCatalogItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "series_catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SeriesCatalog extends EntityAbstract implements RecommendationCatalogItem {

    @Column(nullable = false, unique = true)
    private Long tmdbId;

    @Column(nullable = false)
    private String name;

    @Column
    private String originalName;

    @Column
    private String overview;

    @Column
    private String posterPath;

    @Column
    private String backdropPath;

    @Column
    private LocalDate firstAirDate;

    @Column
    private Double voteAverage;

    @Column
    private Integer voteCount;

    @Column
    private Double popularity;

    @Column(length = 10)
    private String originalLanguage;

    @Column(length = 10)
    private List<String> originCountry;

    @Column
    private String creator;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EditorialCategory editorialCategory;

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public Set<String> getGenres() {
        return Set.of();
    }
}
