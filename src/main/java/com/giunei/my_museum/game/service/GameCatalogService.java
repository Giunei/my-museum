package com.giunei.my_museum.game.service;

import com.giunei.my_museum.game.client.RawgClient;
import com.giunei.my_museum.game.dto.RawgGameResponse;
import com.giunei.my_museum.game.entity.GameCatalog;
import com.giunei.my_museum.game.repository.GameCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameCatalogService {

    private final GameCatalogRepository repository;
    private final RawgClient rawgClient;

    public GameCatalog findByName(String gameName) {
        return repository.findByName(gameName).orElse(null);
    }

    @Transactional
    public GameCatalog findOrCreateByName(String gameName) {
        return repository.findByName(gameName)
                .orElseGet(() -> fetchFromRawgAndSave(gameName));
    }

    @Transactional
    public GameCatalog findOrCreateById(Long rawgId) {
        return repository.findByRawgId(rawgId)
                .orElseGet(() -> fetchFromRawgByIdAndSave(rawgId));
    }

    private GameCatalog fetchFromRawgAndSave(String gameName) {
        RawgGameResponse response = rawgClient.searchGames(gameName, 1, 1);
        if (response == null || response.results() == null || response.results().isEmpty()) {
            return createPlaceholderGame(gameName);
        }

        RawgGameResponse.RawgGameItem item = response.results().get(0);
        return repository.save(mapToEntity(item));
    }

    private GameCatalog fetchFromRawgByIdAndSave(Long rawgId) {
        RawgGameResponse response = rawgClient.searchGames("", 1);
        if (response == null || response.results() == null || response.results().isEmpty()) {
            return createPlaceholderGameById(rawgId);
        }

        RawgGameResponse.RawgGameItem item = response.results().stream()
                .filter(game -> game.id() != null && game.id().equals(rawgId.intValue()))
                .findFirst()
                .orElse(null);

        if (item == null) {
            return createPlaceholderGameById(rawgId);
        }

        return repository.save(mapToEntity(item));
    }

    private GameCatalog mapToEntity(RawgGameResponse.RawgGameItem item) {
        return GameCatalog.builder()
                .rawgId(item.id() != null ? item.id().longValue() : null)
                .name(item.name())
                .build();
    }

    private GameCatalog createPlaceholderGame(String gameName) {
        return GameCatalog.builder()
                .rawgId(null)
                .name(gameName)
                .build();
    }

    private GameCatalog createPlaceholderGameById(Long rawgId) {
        return GameCatalog.builder()
                .rawgId(rawgId)
                .name("Unknown Game")
                .build();
    }
}
