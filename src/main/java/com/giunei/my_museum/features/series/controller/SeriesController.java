package com.giunei.my_museum.features.series.controller;

import com.giunei.my_museum.features.media.dto.UserMediaResponse;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.service.UserMediaService;
import com.giunei.my_museum.features.series.dto.SeasonDetailResponse;
import com.giunei.my_museum.features.series.dto.SeriesDetailResponse;
import com.giunei.my_museum.features.series.dto.SeriesResponse;
import com.giunei.my_museum.features.series.dto.SeriesSummaryResponse;
import com.giunei.my_museum.features.series.dto.WatchingNowResponse;
import com.giunei.my_museum.features.series.service.SeriesService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
@RequiredArgsConstructor
@Validated
public class SeriesController {

    private final SeriesService service;
    private final UserMediaService userMediaService;

    @GetMapping("/search")
    public List<SeriesResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") @Min(0) int page
    ) {
        return service.search(query, page);
    }

    @GetMapping("/curated")
    public List<SeriesResponse> curated() {
        return service.getCuratedSeries();
    }

    @GetMapping("/highlighted")
    public List<UserMediaResponse> highlighted() {
        return userMediaService.getHighlighted(MediaType.SERIES);
    }

    @GetMapping("/summary")
    public SeriesSummaryResponse summary() {
        return service.getSummary();
    }

    @GetMapping("/watching-now")
    public List<WatchingNowResponse> watchingNow() {
        return service.getWatchingNow();
    }

    @GetMapping("/{id}/details")
    public SeriesDetailResponse getSeriesDetails(@PathVariable Long id) {
        return service.getSeriesDetails(id);
    }

    @GetMapping("/{id}/season/{seasonNumber}")
    public SeasonDetailResponse getSeasonDetails(
            @PathVariable Long id,
            @PathVariable Integer seasonNumber
    ) {
        return service.getSeasonDetails(id, seasonNumber);
    }
}
