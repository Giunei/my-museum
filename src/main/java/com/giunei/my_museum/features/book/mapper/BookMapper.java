package com.giunei.my_museum.features.book.mapper;

import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.GoogleBooksApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

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
}
