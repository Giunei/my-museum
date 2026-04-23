package com.giunei.my_museum.features.book.client;

import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.features.book.exeption.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class GoogleBooksClient {

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

    public GoogleBooksApiResponse searchBooks(String query, int page, int size) {
        int startIndex = page * size;

        try {
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
                            .build())
                    .retrieve()
                    .bodyToMono(GoogleBooksApiResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .retryWhen(
                            Retry.backoff(retryMaxAttempts, Duration.ofMillis(firstBackoffMs))
                                    .maxBackoff(Duration.ofSeconds(maxBackoffSeconds))
                                    .jitter(0.5)
                                    .filter(this::isRetriableError)
                                    .onRetryExhaustedThrow((ignoredSpec, signal) ->
                                            new ExternalApiException("Falha após retries", signal.failure())
                                    )
                    )
                    .block(); // continua síncrono
        } catch (Exception ex) {
            Throwable cause = Exceptions.unwrap(ex);

            if (cause instanceof WebClientResponseException responseException) {
                if (responseException.getStatusCode().is4xxClientError()) {
                    throw new ExternalApiException("Erro de requisição ao buscar livros", responseException);
                }
                throw new ExternalApiException("API de livros indisponível", responseException);
            }

            if (cause instanceof SocketTimeoutException) {
                throw new ExternalApiException("Timeout ao chamar API de livros", ex);
            }

            if (cause instanceof ConnectException) {
                throw new ExternalApiException("Erro de conexão com API de livros", ex);
            }

            throw new ExternalApiException("Erro geral ao chamar API externa", ex);
        }
    }

    private boolean isRetriableError(Throwable error) {
        if (error instanceof WebClientResponseException responseException) {
            int status = responseException.getStatusCode().value();
            return status == 429 || responseException.getStatusCode().is5xxServerError();
        }

        return error instanceof WebClientRequestException
                || error instanceof TimeoutException
                || error instanceof ConnectException
                || error instanceof SocketTimeoutException;
    }
}