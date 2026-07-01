package com.giunei.my_museum.features.lol.client;

import com.giunei.my_museum.exceptions.BusinessException;
import com.giunei.my_museum.features.book.exeption.ExternalApiException;
import com.giunei.my_museum.features.lol.dto.RiotAccountResponse;
import com.giunei.my_museum.features.lol.dto.RiotLeagueEntryResponse;
import com.giunei.my_museum.features.lol.dto.RiotSummonerResponse;
import com.giunei.my_museum.features.lol.enums.LolPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class RiotClient {

    private static final ParameterizedTypeReference<List<RiotLeagueEntryResponse>> LEAGUE_ENTRIES_TYPE =
            new ParameterizedTypeReference<>() {};

    private final String apiKey;
    private final WebClient webClient;

    public RiotClient(@Value("${riot.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder().build();
    }

    public RiotAccountResponse getAccountByRiotId(LolPlatform platform, String gameName, String tagLine) {
        String routing = platform.routingRegion();
        String encodedGameName = UriUtils.encodePathSegment(gameName.trim(), StandardCharsets.UTF_8);
        String encodedTagLine = UriUtils.encodePathSegment(tagLine.trim(), StandardCharsets.UTF_8);

        String url = "https://%s.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s"
                .formatted(routing, encodedGameName, encodedTagLine);

        return get(url, RiotAccountResponse.class, "Conta Riot não encontrada");
    }

    public RiotSummonerResponse getSummonerByPuuid(LolPlatform platform, String puuid) {
        String url = "https://%s.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/%s"
                .formatted(platform.getValue(), puuid);

        return get(url, RiotSummonerResponse.class, "Invocador não encontrado na região informada");
    }

    public List<RiotLeagueEntryResponse> getLeagueEntriesByPuuid(LolPlatform platform, String puuid) {
        String url = "https://%s.api.riotgames.com/lol/league/v4/entries/by-puuid/%s"
                .formatted(platform.getValue(), puuid);

        List<RiotLeagueEntryResponse> entries = get(url, LEAGUE_ENTRIES_TYPE, null);
        return entries != null ? entries : List.of();
    }

    private <T> T get(String url, Class<T> responseType, String notFoundMessage) {
        try {
            return webClient.get()
                    .uri(url)
                    .header("X-Riot-Token", apiKey)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            if (notFoundMessage != null) {
                throw new BusinessException(notFoundMessage);
            }
            return null;
        } catch (WebClientResponseException.Forbidden e) {
            throw new ExternalApiException("Chave da API Riot inválida ou sem permissão");
        } catch (WebClientResponseException.TooManyRequests e) {
            throw new ExternalApiException("Limite de requisições da Riot excedido. Tente novamente em instantes.");
        } catch (WebClientResponseException e) {
            log.error("Riot API error {} for URL {}", e.getStatusCode(), url, e);
            throw new ExternalApiException("Erro ao consultar a API da Riot: " + e.getStatusText());
        } catch (Exception e) {
            log.error("Unexpected error calling Riot API for URL {}", url, e);
            throw new ExternalApiException("Erro ao consultar a API da Riot", e);
        }
    }

    private <T> T get(String url, ParameterizedTypeReference<T> responseType, String notFoundMessage) {
        try {
            return webClient.get()
                    .uri(url)
                    .header("X-Riot-Token", apiKey)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            if (notFoundMessage != null) {
                throw new BusinessException(notFoundMessage);
            }
            return null;
        } catch (WebClientResponseException.Forbidden e) {
            throw new ExternalApiException("Chave da API Riot inválida ou sem permissão");
        } catch (WebClientResponseException.TooManyRequests e) {
            throw new ExternalApiException("Limite de requisições da Riot excedido. Tente novamente em instantes.");
        } catch (WebClientResponseException e) {
            log.error("Riot API error {} for URL {}", e.getStatusCode(), url, e);
            throw new ExternalApiException("Erro ao consultar a API da Riot: " + e.getStatusText());
        } catch (Exception e) {
            log.error("Unexpected error calling Riot API for URL {}", url, e);
            throw new ExternalApiException("Erro ao consultar a API da Riot", e);
        }
    }
}
