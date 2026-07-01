package com.giunei.my_museum.features.series.client;

import com.giunei.my_museum.features.series.dto.SeasonDetailResponse;
import com.giunei.my_museum.features.series.dto.SeriesDetailResponse;
import com.giunei.my_museum.features.series.dto.TmdbSeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TmdbSeriesClient {

    private final RestClient tmdbRestClient;

    public TmdbSeriesResponse searchSeries(String query, int page) {
        return tmdbRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/tv")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("language", "pt-BR")
                        .build()
                )
                .retrieve()
                .body(TmdbSeriesResponse.class);
    }

    public SeriesDetailResponse getSeriesDetails(Long seriesId) {
        return tmdbRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}")
                        .queryParam("language", "pt-BR")
                        .build(seriesId)
                )
                .retrieve()
                .body(SeriesDetailResponse.class);
    }

    public SeasonDetailResponse getSeasonDetails(Long seriesId, Integer seasonNumber) {
        return tmdbRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/season/{seasonNumber}")
                        .queryParam("language", "pt-BR")
                        .build(seriesId, seasonNumber)
                )
                .retrieve()
                .body(SeasonDetailResponse.class);
    }
}
