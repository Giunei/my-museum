package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.features.game.client.RawgClient;
import com.giunei.my_museum.features.game.dto.GameResponse;
import com.giunei.my_museum.features.game.dto.RawgGameResponse;
import com.giunei.my_museum.features.game.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameSearchService {

    private final RawgClient rawgClient;
    private final GameMapper gameMapper;

    public List<GameResponse> searchGames(String query, int page) {
        RawgGameResponse response = rawgClient.searchGames(query, page);
        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results().stream()
                .map(item -> {
                    try {
                        return gameMapper.toResponse(item);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

//    public List<GameResponse> getCuratedGames() {
//        RawgGameResponse response = rawgClient.getCuratedGames();
//        if (response == null || response.results() == null) {
//            return List.of();
//        }
//
//        return response.results().stream()
//                .map(item -> {
//                    try {
//                        return gameMapper.toResponse(item);
//                    } catch (Exception e) {
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toList();
//    }
}
