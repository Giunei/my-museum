package com.giunei.my_museum.game.repository;

import com.giunei.my_museum.game.entity.GameCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameCatalogRepository extends JpaRepository<GameCatalog, Long> {

    Optional<GameCatalog> findByName(String name);

    Optional<GameCatalog> findByRawgId(Long rawgId);

    List<GameCatalog> findByRawgIdBetween(Long minRawgId, Long maxRawgId);
}
