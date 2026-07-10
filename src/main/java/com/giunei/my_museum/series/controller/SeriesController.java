package com.giunei.my_museum.series.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.UserMediaResponse;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.UserMediaService;
import com.giunei.my_museum.recommendation.dto.RecommendationSectionResponse;
import com.giunei.my_museum.recommendation.series.dto.SeriesRecommendationCardResponse;
import com.giunei.my_museum.recommendation.series.service.SeriesRecommendationFacade;
import com.giunei.my_museum.series.dto.SeasonDetailResponse;
import com.giunei.my_museum.series.dto.SeriesDetailResponse;
import com.giunei.my_museum.series.dto.SeriesResponse;
import com.giunei.my_museum.series.dto.SeriesSummaryResponse;
import com.giunei.my_museum.series.dto.WatchingNowResponse;
import com.giunei.my_museum.series.service.SeriesService;
import com.giunei.my_museum.user.entity.User;
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
    private final SeriesRecommendationFacade recommendationFacade;

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

    @GetMapping("/recommendations/for-you")
    public List<SeriesRecommendationCardResponse> forYou(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.recommendedForYou(user, limitPerBucket);
    }

    @GetMapping("/recommendations/maybe-you-like")
    public RecommendationSectionResponse<SeriesRecommendationCardResponse> maybeYouLike(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.maybeYouLike(user, limitPerBucket);
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
