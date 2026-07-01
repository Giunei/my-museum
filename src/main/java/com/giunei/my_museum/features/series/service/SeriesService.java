package com.giunei.my_museum.features.series.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.series.client.TmdbSeriesClient;
import com.giunei.my_museum.features.series.dto.SeasonDetailResponse;
import com.giunei.my_museum.features.series.dto.SeriesDetailResponse;
import com.giunei.my_museum.features.series.dto.SeriesListCache;
import com.giunei.my_museum.features.series.dto.SeriesResponse;
import com.giunei.my_museum.features.series.dto.SeriesSummaryResponse;
import com.giunei.my_museum.features.series.dto.WatchingNowResponse;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesCacheService cacheService;
    private final UserMediaRepository userMediaRepository;
    private final TmdbSeriesClient tmdbSeriesClient;

    public List<SeriesResponse> search(String query, int page) {
        return cacheService.search(query, page);
    }

    public List<SeriesResponse> getCuratedSeries() {
        try {
            return cacheService.getCuratedSeries()
                    .series();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public SeriesSummaryResponse getSummary() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();
            int seriesWatched = (int) userMediaRepository.countByUserAndTypeAndCompletedTrue(user, MediaType.SERIES);
            
            List<String> favoriteGenres = userMediaRepository.findByUserAndType(user, MediaType.SERIES, org.springframework.data.domain.Pageable.unpaged())
                    .stream()
                    .filter(m -> m.getTitle() != null)
                    .map(m -> extractGenre(m.getTitle()))
                    .filter(g -> !g.equals("Desconhecido"))
                    .collect(Collectors.toList());
            
            int totalSeries = (int) userMediaRepository.findByUserAndType(user, MediaType.SERIES, org.springframework.data.domain.Pageable.unpaged())
                    .getTotalElements();
            
            return new SeriesSummaryResponse(totalSeries, seriesWatched, null, favoriteGenres);
        } catch (Exception e) {
            System.err.println("Error loading series summary: " + e.getMessage());
            e.printStackTrace();
            return new SeriesSummaryResponse(0, 0, null, List.of());
        }
    }

    private String extractGenre(String title) {
        return "Desconhecido";
    }

    public List<WatchingNowResponse> getWatchingNow() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();

            return userMediaRepository.findByUserAndTypeAndStatus(user, MediaType.SERIES, MediaStatus.IN_PROGRESS, org.springframework.data.domain.Pageable.unpaged())
                    .stream()
                    .map(m -> new WatchingNowResponse(
                            m.getId(),
                            m.getTitle(),
                            m.getThumbnail(),
                            m.getPageCount(),
                            m.getCurrentSeason(),
                            m.getCurrentEpisode()
                    ))
                    .limit(6)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading watching now: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public SeriesDetailResponse getSeriesDetails(Long id) {
        try {
            return tmdbSeriesClient.getSeriesDetails(id);
        } catch (Exception e) {
            System.err.println("Error loading series details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load series details", e);
        }
    }

    public SeasonDetailResponse getSeasonDetails(Long seriesId, Integer seasonNumber) {
        try {
            return tmdbSeriesClient.getSeasonDetails(seriesId, seasonNumber);
        } catch (Exception e) {
            System.err.println("Error loading season details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load season details", e);
        }
    }
}
