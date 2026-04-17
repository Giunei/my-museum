package com.giunei.my_museum.features.book.mapper;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final ObjectMapper objectMapper;

    public BookResponse toResponse(GoogleBooksApiResponse.Item item) {
        var info = item.volumeInfo();
        if (info == null) return null;

        String thumbnail = (info.imageLinks() != null) ? info.imageLinks().thumbnail() : null;
        var authors = (info.authors() != null) ? info.authors() : Collections.<String>emptyList();

        return new BookResponse(
                item.id(),
                info.title(),
                authors,
                info.description(),
                thumbnail,
                info.language()
        );
    }

    public List<BookResponse> toResponseList(GoogleBooksApiResponse response) {

        if (response == null || response.items() == null) {
            return List.of();
        }

        return response.items().stream()
                .map(item -> {

                    var volumeInfo = item.volumeInfo();

                    String id = item.id();
                    String title = volumeInfo.title();

                    List<String> authors = volumeInfo.authors() != null
                            ? volumeInfo.authors()
                            : List.of();

                    String description = volumeInfo.description();

                    String thumbnail = volumeInfo.imageLinks() != null
                            ? volumeInfo.imageLinks().thumbnail()
                            : null;

                    String language = volumeInfo.language() != null
                            ? volumeInfo.language()
                            : "unknown";

                    return new BookResponse(
                            id,
                            title,
                            authors,
                            description,
                            thumbnail,
                            language
                    );
                })
                .toList();
    }
}
