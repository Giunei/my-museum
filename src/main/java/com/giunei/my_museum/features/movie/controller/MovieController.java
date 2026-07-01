package com.giunei.my_museum.features.movie.controller;

import com.giunei.my_museum.features.media.dto.UserMediaResponse;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.service.UserMediaService;
import com.giunei.my_museum.features.movie.dto.MovieResponse;
import com.giunei.my_museum.features.movie.dto.MovieSummaryResponse;
import com.giunei.my_museum.features.movie.service.MovieService;
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
}
