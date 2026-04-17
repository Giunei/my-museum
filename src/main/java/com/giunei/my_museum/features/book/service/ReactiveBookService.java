package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.features.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ReactiveBookService {

    private final WebClient webClient;
    private final BookMapper mapper;

    @Value("${google.books.api.key}")
    private String apiKey;


    public Flux<BookResponse> search(String query, int page, int size) {
        int startIndex = page * size;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("startIndex", startIndex)
                        .queryParam("maxResults", size)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBooksApiResponse.class)
                .flatMapMany(response ->
                        Flux.fromIterable(
                                mapper.toResponseList(response)
                        )
                );
    }
}


