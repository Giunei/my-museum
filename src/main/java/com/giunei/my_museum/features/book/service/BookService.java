package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.BookSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookCacheService cacheService;

    public List<BookResponse> search(BookSearchRequest request) {
        return cacheService.search(request);
    }

    public List<BookResponse> getCuratedBooks() {
        return cacheService.getCuratedBooks()
                .books();
    }
}


