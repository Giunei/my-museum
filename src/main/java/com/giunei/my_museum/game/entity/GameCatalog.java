package com.giunei.my_museum.game.entity;

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
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "game_catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GameCatalog extends EntityAbstract implements RecommendationCatalogItem {

    @Column(nullable = false, unique = true)
    private Long rawgId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EditorialCategory editorialCategory = EditorialCategory.BESTSELLER;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_catalog_genre", joinColumns = @JoinColumn(name = "game_catalog_id"))
    @Column(name = "genre", nullable = false)
    @Builder.Default
    private Set<String> genres = new LinkedHashSet<>();

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getCreator() {
        return null;
    }
}
