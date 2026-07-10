package com.giunei.my_museum.recommendation.game.provider;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.game.client.RawgClient;
import com.giunei.my_museum.game.dto.GameResponse;
import com.giunei.my_museum.game.dto.RawgGameResponse;
import com.giunei.my_museum.game.mapper.GameMapper;
import com.giunei.my_museum.recommendation.provider.RecommendationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class GameRecommendationProvider implements RecommendationProvider<GameResponse> {

    private final RawgClient rawgClient;
    private final GameMapper gameMapper;

    @Override
    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "games:recommendation-detail", key = "#title", unless = "#result == null")
    public GameResponse fetch(String title, String creator) {
        try {
            RawgGameResponse response = rawgClient.searchGames(title, 1, 1);
            if (response == null || response.results() == null || response.results().isEmpty()) {
                return null;
            }
            return gameMapper.toSearchResponse(response.results().get(0));
        } catch (Exception ignored) {
            return null;
        }
    }
}
