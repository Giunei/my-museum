package com.giunei.my_museum.recommendation.series.provider;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.recommendation.provider.RecommendationProvider;
import com.giunei.my_museum.series.client.TmdbSeriesClient;
import com.giunei.my_museum.series.dto.SeriesResponse;
import com.giunei.my_museum.series.mapper.SeriesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeriesRecommendationProvider
        implements RecommendationProvider<SeriesResponse> {

    private final TmdbSeriesClient tmdbSeriesClient;
    private final SeriesMapper seriesMapper;

    @Override
    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "series:recommendation-detail", key = "#title + '::' + #creator", unless = "#result == null")
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
