package com.giunei.my_museum.features.game.entity;

import com.giunei.my_museum.core.EntityAbstract;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "game_catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class GameCatalog extends EntityAbstract {

    @Column(nullable = false, unique = true)
    private Long rawgId;

    @Column(nullable = false)
    private String name;
}
