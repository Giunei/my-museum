package com.giunei.my_museum.features.game.controller;

import com.giunei.my_museum.features.game.dto.GameResponse;
import com.giunei.my_museum.features.game.service.ReactiveGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/reactive/games")
@RequiredArgsConstructor
public class ReactiveGameController {

    private final ReactiveGameService reactiveGameService;

    @GetMapping("/search")
    public Flux<GameResponse> searchGames(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return reactiveGameService.searchGames(query, page);
    }

    @GetMapping(value = "/curated/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GameResponse> curated() {
        return reactiveGameService.getCuratedGames();
    }
}
