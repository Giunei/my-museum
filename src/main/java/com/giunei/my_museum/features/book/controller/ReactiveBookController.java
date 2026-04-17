package com.giunei.my_museum.features.book.controller;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.service.ReactiveBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/reactive/books")
@RequiredArgsConstructor
public class ReactiveBookController {

    private final ReactiveBookService service;

    @GetMapping("/search")
    public Flux<BookResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.search(query, page, size);
    }

}
