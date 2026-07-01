package com.giunei.my_museum.features.game.client;

import com.giunei.my_museum.features.game.dto.SteamAchievementsResponse;
import com.giunei.my_museum.features.game.dto.SteamOwnedGamesResponse;
import com.giunei.my_museum.features.game.dto.SteamPlayerSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class SteamClient {

    private final WebClient steamWebClient;

    @Value("${steam.api.key}")
    private String apiKey;

    public SteamPlayerSummaryResponse getPlayer(String steamId) {
        return steamWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/ISteamUser/GetPlayerSummaries/v2")
                                .queryParam("key", apiKey)
                                .queryParam("steamids", steamId)
                                .build()
                )
                .retrieve()
                .bodyToMono(SteamPlayerSummaryResponse.class)
                .block();
    }

    public SteamOwnedGamesResponse getOwnedGames(String steamId) {
        return steamWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/IPlayerService/GetOwnedGames/v1")
                                .queryParam("key", apiKey)
                                .queryParam("steamid", steamId)
                                .queryParam("include_appinfo", true)
                                .queryParam("include_played_free_games", true)
                                .build()
                )
                .retrieve()
                .bodyToMono(SteamOwnedGamesResponse.class)
                .block();
    }

    public SteamAchievementsResponse getAchievements(String steamId, String appId) {
        return steamWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/ISteamUserStats/GetPlayerAchievements/v1")
                                .queryParam("key", apiKey)
                                .queryParam("steamid", steamId)
                                .queryParam("appid", appId)
                                .queryParam("l", "en")
                                .build()
                )
                .retrieve()
                .bodyToMono(SteamAchievementsResponse.class)
                .block();
    }
}
