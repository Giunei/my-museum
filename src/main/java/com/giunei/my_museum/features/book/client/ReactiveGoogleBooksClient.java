package com.giunei.my_museum.features.book.client;

import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ReactiveGoogleBooksClient.class);

    // Partial response reduz bastante o tamanho do JSON retornado.
    private static final String GOOGLE_BOOKS_FIELDS =
            "totalItems,items(id,volumeInfo(title,authors,description,imageLinks/thumbnail,language,pageCount))";

    private final WebClient webClient;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Value("${google.books.api.timeout-seconds:4}")
    private long timeoutSeconds;

    @Value("${google.books.api.retry.max-attempts:3}")
    private long retryMaxAttempts;

    @Value("${google.books.api.retry.first-backoff-ms:200}")
    private long firstBackoffMs;

    @Value("${google.books.api.retry.max-backoff-seconds:2}")
    private long maxBackoffSeconds;

    public Mono<GoogleBooksApiResponse> searchBooks(String query, int page, int size) {
        int startIndex = page * size;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("startIndex", startIndex)
                        .queryParam("maxResults", size)
                        .queryParam("printType", "books")
                        .queryParam("langRestrict", "pt")
                        .queryParam("fields", GOOGLE_BOOKS_FIELDS)
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(GoogleBooksApiResponse.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(
                        Retry.backoff(retryMaxAttempts, Duration.ofMillis(firstBackoffMs))
                                .maxBackoff(Duration.ofSeconds(maxBackoffSeconds))
                                .jitter(0.5)
                                .filter(this::isRetriableError)
                                .doBeforeRetry(signal -> log.debug(
                                        "Retrying Google Books request (attempt={}): {}",
                                        signal.totalRetries() + 1,
                                        signal.failure() != null ? signal.failure().toString() : "unknown"
                                ))
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