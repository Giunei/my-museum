package com.giunei.my_museum.movie.controller;

import com.giunei.my_museum.movie.dto.MovieResponse;
import com.giunei.my_museum.movie.service.ReactiveMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/reactive/movies")
@RequiredArgsConstructor
public class ReactiveMovieController {

    private final ReactiveMovieService service;

    @GetMapping(value = "/curated/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieResponse> curated() {
        return service.getCuratedMovies()
                .onErrorResume(e -> Flux.empty());
    }
}
