package com.giunei.my_museum.features.book.controller;

import com.giunei.my_museum.features.book.dto.BookPageResponse;
import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.service.ReactiveBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reactive/books")
@RequiredArgsConstructor
public class ReactiveBookController {

    private final ReactiveBookService service;

    @GetMapping("/search")
    public Mono<BookPageResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.search(query, page, size);
    }

    @GetMapping(value = "/curated/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookResponse> curated() {
        return service.getCuratedBooks();
    }
}
