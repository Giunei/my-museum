package com.giunei.my_museum.features.game.repository;

import com.giunei.my_museum.features.game.entity.GameCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameCatalogRepository extends JpaRepository<GameCatalog, Long> {

    Optional<GameCatalog> findByName(String name);

    Optional<GameCatalog> findByRawgId(Long rawgId);
}
