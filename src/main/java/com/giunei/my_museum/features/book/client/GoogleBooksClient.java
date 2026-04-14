package com.giunei.my_museum.features.book.client;

import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.features.book.exeption.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Component
@RequiredArgsConstructor
public class GoogleBooksClient {

    private final RestClient restClient;

    @Value("${google.books.api.key}")
    private String apiKey;

    public GoogleBooksApiResponse searchBooks(String query, int page, int size) {
        int startIndex = page * size;

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", query)
                            .queryParam("startIndex", startIndex)
                            .queryParam("maxResults", size)
                            .queryParam("key", apiKey)
                            .queryParam("language", "pt")
                            .build())
                    .retrieve()
                    .body(GoogleBooksApiResponse.class);
        } catch (RestClientResponseException ex) {
            // HTTP 4xx ou 5xx
            if (ex.getStatusCode().is4xxClientError()){
                throw new ExternalApiException("Erro ao buscar para a API de livros", ex);
            }
            if (ex.getStatusCode().is5xxServerError()) {
                throw new ExternalApiException("API de livros está indisponível (5xx)");
            }

            throw new ExternalApiException("Erro inesperado da API externa", ex);
        } catch (Exception ex) {
            // Timeout ou conexão
            if (ex.getCause() instanceof SocketTimeoutException) {
                throw new ExternalApiException("Timeout ao chamar API de livros");
            }
            if (ex.getCause() instanceof ConnectException) {
                throw new ExternalApiException("Erro de conexão com API de livros");
            }

            throw new ExternalApiException("Erro geral ao chamar API externa", ex);
        }
    }
}
