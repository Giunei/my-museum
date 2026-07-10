package com.giunei.my_museum.game.service;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.game.client.RawgClient;
import com.giunei.my_museum.game.dto.GameResponse;
import com.giunei.my_museum.game.dto.RawgGameResponse;
import com.giunei.my_museum.game.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameSearchService {

    private final RawgClient rawgClient;
    private final GameMapper gameMapper;

    @Cacheable(
            cacheManager = CacheManagers.RECOMMENDATION,
            value = "games:search",
            key = "#query.toLowerCase().trim() + '::' + #page",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<GameResponse> searchGames(String query, int page) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        RawgGameResponse response = rawgClient.searchGames(query.trim(), page);
        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results().stream()
                .map(item -> {
                    try {
                        return gameMapper.toSearchResponse(item);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
