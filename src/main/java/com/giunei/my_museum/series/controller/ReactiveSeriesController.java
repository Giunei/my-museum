package com.giunei.my_museum.series.controller;

import com.giunei.my_museum.series.dto.SeriesResponse;
import com.giunei.my_museum.series.service.ReactiveSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/reactive/series")
@RequiredArgsConstructor
public class ReactiveSeriesController {

    private final ReactiveSeriesService service;

    @GetMapping(value = "/curated/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SeriesResponse> curated() {
        return service.getCuratedSeries()
                .onErrorResume(e -> Flux.empty());
    }
}
