package com.giunei.my_museum.book.client;

import com.giunei.my_museum.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.common.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class GoogleBooksClient {

    private static final String GOOGLE_BOOKS_FIELDS =
            "totalItems,items(id,volumeInfo(title,authors,description,imageLinks/thumbnail,infoLink,previewLink,language,pageCount,averageRating,ratingsCount,categories))";

    private static final String GOOGLE_BOOK_VOLUME_FIELDS =
            "id,volumeInfo(title,authors,categories)";

    private final RestTemplate googleBooksRestTemplate;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Value("${google.books.api.default-language:pt}")
    private String defaultLanguage;

    public GoogleBooksApiResponse searchBooks(String query, int page, int size) {
        return searchBooks(query, page, size, null, null);
    }

    public GoogleBooksApiResponse searchBooks(String query, int page, int size, String language, String orderBy) {
        int startIndex = page * size;
        String safeLanguage = (language == null || language.isBlank())
                ? defaultLanguage
                : language.toLowerCase(Locale.ROOT);
        String safeOrderBy = (orderBy == null || orderBy.isBlank()) ? "relevance" : orderBy;

        // use fromUriString to avoid IDE/resolution issues with fromHttpUrl in some classpath setups
        String url = UriComponentsBuilder.fromUriString("https://www.googleapis.com/books/v1/volumes")
                .queryParam("q", query)
                .queryParam("startIndex", startIndex)
                .queryParam("maxResults", size)
                .queryParam("printType", "books")
                .queryParam("langRestrict", safeLanguage)
                .queryParam("orderBy", safeOrderBy)
                .queryParam("fields", GOOGLE_BOOKS_FIELDS)
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        try {
            return googleBooksRestTemplate.getForObject(url, GoogleBooksApiResponse.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                throw new ExternalApiException("Erro de requisição ao buscar livros", ex);
            }
            throw new ExternalApiException("API de livros indisponível", ex);
        } catch (RestClientException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

            switch (cause) {
                case SocketTimeoutException socketTimeoutException -> throw new ExternalApiException("Timeout ao chamar API de livros", socketTimeoutException);
                case ConnectException connectException -> throw new ExternalApiException("Erro de conexão com API de livros", connectException);
                default -> throw new ExternalApiException("Erro geral ao chamar API externa", ex);
            }
        }
    }

    public GoogleBooksApiResponse.Item getVolumeById(String volumeId) {
        if (volumeId == null || volumeId.isBlank()) {
            return null;
        }

        String url = UriComponentsBuilder.fromUriString("https://www.googleapis.com/books/v1/volumes/{volumeId}")
                .queryParam("fields", GOOGLE_BOOK_VOLUME_FIELDS)
                .queryParam("key", apiKey)
                .buildAndExpand(volumeId)
                .toUriString();

        try {
            return googleBooksRestTemplate.getForObject(url, GoogleBooksApiResponse.Item.class);
        } catch (RestClientException ex) {
            return null;
        }
    }
}