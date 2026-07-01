package com.giunei.my_museum.features.recommendation.series.provider;

import com.giunei.my_museum.features.recommendation.provider.RecommendationProvider;
import com.giunei.my_museum.features.series.client.TmdbSeriesClient;
import com.giunei.my_museum.features.series.dto.SeriesResponse;
import com.giunei.my_museum.features.series.mapper.SeriesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeriesRecommendationProvider
        implements RecommendationProvider<SeriesResponse> {

    private final TmdbSeriesClient tmdbSeriesClient;
    private final SeriesMapper seriesMapper;

    @Override
    public SeriesResponse fetch(String title, String creator) {
        try {
            var response = tmdbSeriesClient.searchSeries(title, 1);
            if (response == null || response.results() == null || response.results().isEmpty()) {
                return null;
            }
            return seriesMapper.toResponse(response.results().get(0));
        } catch (Exception ignored) {
            return null;
        }
    }
}
