package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.features.book.client.GoogleBooksClient;
import com.giunei.my_museum.features.book.dto.BookListCache;
import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.BookSearchRequest;
import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.features.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCacheService {

    private final GoogleBooksClient client;
    private final BookMapper mapper;
    private final BookQueryBuilder queryBuilder;

    private static final Logger log = LoggerFactory.getLogger(BookCacheService.class);

    private static final List<String> CURATED = List.of(
            "A Metamorfose - Franz Kafka",
            "Verity - Colleen Hoover",
            "A Cabeça do Santo - Socorro Acioli",
            "A Biblioteca da Meia-Noite - Matt Haig",
            "Tudo é Rio - Carla Madeira",
            "Hábitos Atômicos - James Clear",
            "A Empregada - Freida McFadden",
            "A Hora da Estrela - Clarice Lispector",
            "O Homem Mais Rico da Babilônia - George S. Clason",
            "A Psicologia Financeira - Morgan Housel"

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
                .map(term -> {
                            try {
                                return client.searchBooks(term, 0, 1);
                            } catch (Exception e) {
                                log.warn("Erro ao buscar: {} - {}", term, e.getMessage());
                                return null;
                            }
                        }
                )
                .filter(result -> result != null && result.items() != null)
                .flatMap(result -> result.items().stream())
                .map(mapper::toResponse)
                .toList();

        return new BookListCache(books);
    }
}
