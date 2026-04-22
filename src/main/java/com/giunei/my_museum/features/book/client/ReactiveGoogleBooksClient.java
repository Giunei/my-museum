package com.giunei.my_museum.features.book.client;

import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class ReactiveGoogleBooksClient {

    private final WebClient webClient;

    @Value("${google.books.api.key}")
    private String apiKey;

    public Mono<GoogleBooksApiResponse> searchBooks(String query, int page, int size) {
        int startIndex = page * size;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("startIndex", startIndex)
                        .queryParam("maxResults", size)
                        .queryParam("key", apiKey)
                        .queryParam("langRestrict", "pt")
                        .build()
                )
                .retrieve()
                .bodyToMono(GoogleBooksApiResponse.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(300))
                                .maxBackoff(Duration.ofSeconds(2))
                                .jitter(0.5)
                                .filter(this::isRetriableError)
                );
    }

    private boolean isRetriableError(Throwable error) {
        if (error instanceof WebClientResponseException responseException) {
            int status = responseException.getStatusCode().value();
            return status == 429 || responseException.getStatusCode().is5xxServerError();
        }

        return error instanceof WebClientRequestException || error instanceof TimeoutException;
    }
}