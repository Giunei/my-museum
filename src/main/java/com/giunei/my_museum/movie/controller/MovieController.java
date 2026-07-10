package com.giunei.my_museum.movie.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.UserMediaResponse;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.UserMediaService;
import com.giunei.my_museum.movie.dto.MovieResponse;
import com.giunei.my_museum.movie.dto.MovieSummaryResponse;
import com.giunei.my_museum.movie.service.MovieService;
import com.giunei.my_museum.recommendation.dto.RecommendationSectionResponse;
import com.giunei.my_museum.recommendation.movie.dto.MovieRecommendationCardResponse;
import com.giunei.my_museum.recommendation.movie.service.MovieRecommendationFacade;
import com.giunei.my_museum.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Validated
public class MovieController {

    private final MovieService service;
    private final UserMediaService userMediaService;
    private final MovieRecommendationFacade recommendationFacade;

    @GetMapping("/search")
    public List<MovieResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") @Min(0) int page
    ) {
        return service.search(query, page);
    }

    @GetMapping("/curated")
    public List<MovieResponse> curated() {
        return service.getCuratedMovies();
    }

    @GetMapping("/highlighted")
    public List<UserMediaResponse> highlighted() {
        return userMediaService.getHighlighted(MediaType.MOVIE);
    }

    @GetMapping("/summary")
    public MovieSummaryResponse summary() {
        return service.getSummary();
    }

    @GetMapping("/recommendations/for-you")
    public List<MovieRecommendationCardResponse> forYou(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.recommendedForYou(user, limitPerBucket);
    }

    @GetMapping("/recommendations/maybe-you-like")
    public RecommendationSectionResponse<MovieRecommendationCardResponse> maybeYouLike(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.maybeYouLike(user, limitPerBucket);
    }
}
