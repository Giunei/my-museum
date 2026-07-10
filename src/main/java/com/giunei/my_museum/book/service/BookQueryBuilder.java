package com.giunei.my_museum.book.service;

import com.giunei.my_museum.book.dto.BookSearchRequest;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class BookQueryBuilder {

    public String build(BookSearchRequest request) {
        List<String> parts = new ArrayList<>();

        appendTextPart(parts, request.title(), "intitle:");
        appendTextPart(parts, request.author(), "inauthor:");
        appendFreeText(parts, request.query());

        if (request.genres() != null) {
            request.genres().stream()
                    .filter(this::hasText)
                    .map(this::normalizeToken)
                    .forEach(genre -> parts.add("subject:" + genre));
        }

        return parts.stream()
                .filter(this::hasText)
                .collect(Collectors.joining(" "))
                .trim();
    }

    public String normalizeLanguage(String language) {
        if (!hasText(language)) {
            return null;
        }

        return normalizeToken(language).toLowerCase(Locale.ROOT);
    }

    private void appendTextPart(List<String> parts, String value, String prefix) {
        if (!hasText(value)) {
            return;
        }

        parts.add(prefix + "\"" + normalizeText(value) + "\"");
    }

    private void appendFreeText(List<String> parts, String value) {
        if (!hasText(value)) {
            return;
        }

        parts.add(normalizeText(value));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeText(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\p{Punct}+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String normalizeToken(String value) {
        return normalizeText(value).toLowerCase(Locale.ROOT);
    }
}
