package com.giunei.my_museum.features.book.client;

import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.features.book.exeption.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Component
@RequiredArgsConstructor
public class GoogleBooksClient {

    private final WebClient webClient;

    private final RestClient restClient;

    @Value("${google.books.api.key}")
    private String apiKey;

    public GoogleBooksApiResponse searchBooks(String query, int page, int size) {
        int startIndex = page * size;

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", query)
                            .queryParam("startIndex", startIndex)
                            .queryParam("maxResults", size)
                            .queryParam("key", apiKey)
                            .queryParam("langRestrict", "pt")
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new ExternalApiException("Erro 4xx ao buscar livros: " + body))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new ExternalApiException("API de livros indisponível (5xx): " + body))
                    )
                    .bodyToMono(GoogleBooksApiResponse.class)
                    .block(); // continua síncrono
        } catch (Exception ex) {
            Throwable cause = Exceptions.unwrap(ex);

            if (cause instanceof SocketTimeoutException) {
                throw new ExternalApiException("Timeout ao chamar API de livros", ex);
            }

            if (cause instanceof ConnectException) {
                throw new ExternalApiException("Erro de conexão com API de livros", ex);
            }

            throw new ExternalApiException("Erro geral ao chamar API externa", ex);
        }
    }
}