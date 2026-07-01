package com.giunei.my_museum.features.series.service;

import com.giunei.my_museum.features.series.dto.TmdbSeriesResponse;
import com.giunei.my_museum.features.series.dto.SeriesListCache;
import com.giunei.my_museum.features.series.dto.SeriesResponse;
import com.giunei.my_museum.features.series.mapper.SeriesMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SeriesCacheService {

    private static final Logger log = LoggerFactory.getLogger(SeriesCacheService.class);

    private final ReactiveSeriesService reactiveService;
    private final SeriesMapper mapper;

    @Cacheable(value = "series:search",
            key = "#query + '-' + #page")
    public List<SeriesResponse> search(String query, int page) {
        return reactiveService.search(query, page)
                .map(response -> {
                    if (response.results() == null) {
                        return Collections.<SeriesResponse>emptyList();
                    }
                    return response.results().stream()
                            .map(mapper::toResponse)
                            .filter(Objects::nonNull)
                            .toList();
                })
                .block();
    }

    @Cacheable(value = "series:curated")
    public SeriesListCache getCuratedSeries() {
        List<SeriesResponse> series = reactiveService.getCuratedSeries()
                .collectList()
                .block();

        return new SeriesListCache(series != null ? series : Collections.emptyList());
    }
}
