package com.giunei.my_museum.book.client;

import com.giunei.my_museum.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.common.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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

    private static final Logger log = LoggerFactory.getLogger(GoogleBooksClient.class);

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
        ensureApiKeyConfigured();

        int startIndex = page * size;
        String safeLanguage = (language == null || language.isBlank())
                ? defaultLanguage
                : language.toLowerCase(Locale.ROOT);
        String safeOrderBy = (orderBy == null || orderBy.isBlank()) ? "relevance" : orderBy;

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
            throw translateHttpError(ex);
        } catch (RestClientException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

            switch (cause) {
                case SocketTimeoutException socketTimeoutException -> throw new ExternalApiException(
                        "Timeout ao chamar API de livros", socketTimeoutException);
                case ConnectException connectException -> throw new ExternalApiException(
                        "Erro de conexão com API de livros", connectException);
                default -> throw new ExternalApiException("Erro geral ao chamar API externa", ex);
            }
        }
    }

    public GoogleBooksApiResponse.Item getVolumeById(String volumeId) {
        if (volumeId == null || volumeId.isBlank()) {
            return null;
        }
        if (!StringUtils.hasText(apiKey)) {
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

    private void ensureApiKeyConfigured() {
        if (!StringUtils.hasText(apiKey)) {
            throw new ExternalApiException("GOOGLE_BOOKS_API_KEY não configurada");
        }
    }

    private ExternalApiException translateHttpError(HttpStatusCodeException ex) {
        HttpStatusCode status = ex.getStatusCode();
        int code = status.value();
        log.warn("Google Books API HTTP {}: {}", code, truncate(ex.getResponseBodyAsString()));

        if (code == 429) {
            return new ExternalApiException(
                    "Limite da API de livros excedido (Google Books). Tente novamente em breve.",
                    ex
            );
        }
        if (code == 403) {
            return new ExternalApiException(
                    "Acesso negado à API de livros. Verifique a GOOGLE_BOOKS_API_KEY e a cota no Google Cloud.",
                    ex
            );
        }
        if (status.is4xxClientError()) {
            return new ExternalApiException("Erro de requisição ao buscar livros", ex);
        }
        return new ExternalApiException("API de livros indisponível", ex);
    }

    private static String truncate(String body) {
        if (body == null || body.isBlank()) {
            return "(empty body)";
        }
        return body.length() <= 300 ? body : body.substring(0, 300) + "...";
    }
}
