package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.features.book.client.GoogleBooksClient;
import com.giunei.my_museum.features.book.dto.BookListCache;
import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.BookSearchRequest;
import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.features.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCacheService {

    private final GoogleBooksClient client;
    private final BookMapper mapper;
    private final BookQueryBuilder queryBuilder;
    private final ObjectMapper objectMapper;

    private static final List<String> CURATED = List.of(
            "a escola do bem e do mal",
            "senhor dos aneis",
            "game of thrones",
            "percy jackson",
            "hobbit"
    );

    @Cacheable(value = "books:search",
            key = "#request.query + '-' + #request.genres + '-' + #request.page + '-' + #request.size")
    public List<BookResponse> search(BookSearchRequest request) {
        String query = queryBuilder.build(request);

        int page = request.page();
        int size = request.size() != 0 ? Math.min(request.size(), 20) : 10;

        GoogleBooksApiResponse response = client.searchBooks(query, page, size);

        return response.items() != null ? response.items().stream()
                .map(mapper::toResponse)
                .toList() : List.of();
    }

    @Cacheable(value = "books:curated")
    public BookListCache getCuratedBooks() {
        List<BookResponse> books = CURATED.stream()
                .map(term -> client.searchBooks(term, 0, 1))
                .filter(result -> result != null && result.items() != null)
                .flatMap(result -> result.items().stream())
                .map(mapper::toResponse)
                .toList();

        return new BookListCache(books);
    }
}
