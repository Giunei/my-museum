package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.features.book.client.ReactiveGoogleBooksClient;
import com.giunei.my_museum.features.book.dto.BookPageResponse;
import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReactiveBookService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveBookService.class);

    private final ReactiveGoogleBooksClient client;
    private final BookMapper mapper;

    @Value("${google.books.api.curated.concurrency:4}")
    private int curatedConcurrency;

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

    public Flux<BookResponse> getCuratedBooks() {
        return Flux.fromIterable(CURATED)
                // Executa em paralelo com limite para evitar burst excessivo.
                .flatMap(term ->
                                client.searchBooks(term, 0, 1)
                                        .onErrorResume(e -> {
                                            log.warn("Failed to fetch curated book for term='{}': {}", term, e.toString());
                                            return Mono.empty();
                                        })
                                        .flatMapMany(response -> response.items() == null
                                                ? Flux.empty()
                                                : Flux.fromIterable(response.items()))
                                        .map(item -> {
                                            try {
                                                return mapper.toResponse(item);
                                            } catch (Exception e) {
                                                log.warn("Failed to map Google Books item for term='{}': {}", term, e.toString());
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull),
                        curatedConcurrency
                )
                .onErrorContinue((e, obj) ->
                        log.warn("Ignoring stream error for obj='{}': {}", obj, e.toString()));
    }

    @Cacheable(value = "books:search",
            key = "#query + '-' + #page + '-' + #size")
    public Mono<BookPageResponse> search(String query, int page, int size) {
        int safeSize = Math.min(size, 20);

        return client.searchBooks(query, page, safeSize)
                .map(response -> {
                    List<BookResponse> books = mapper.toResponseList(response);

                    boolean hasNext = response.totalItems() != null &&
                            (page + 1) * safeSize < response.totalItems();

                    return new BookPageResponse(
                            books,
                            page,
                            safeSize,
                            hasNext
                    );
                });
    }
}
