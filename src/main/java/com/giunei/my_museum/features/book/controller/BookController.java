package com.giunei.my_museum.features.book.controller;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.BookSearchRequest;
import com.giunei.my_museum.features.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    @GetMapping("/search")
    public List<BookResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BookSearchRequest request = new BookSearchRequest(query, genres, page, size);
        return service.search(request);
    }

    @GetMapping("/curated")
    public List<BookResponse> curated() {
        return service.getCuratedBooks();
    }
}
