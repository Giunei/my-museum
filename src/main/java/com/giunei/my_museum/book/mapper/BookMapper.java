package com.giunei.my_museum.book.mapper;

import com.giunei.my_museum.book.dto.BookResponse;
import com.giunei.my_museum.book.dto.GoogleBooksApiResponse;
import com.giunei.my_museum.media.dto.UserCollectionInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BookMapper {

    public BookResponse toResponse(GoogleBooksApiResponse.Item item) {
        return toResponse(item, null);
    }

    public BookResponse toResponse(GoogleBooksApiResponse.Item item, UserCollectionInfo userCollectionInfo) {
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
                info.infoLink(),
                info.previewLink(),
                info.language(),
                info.pageCount(),
                info.averageRating(),
                info.ratingsCount(),
                userCollectionInfo
        );
    }

    public List<BookResponse> toResponseList(GoogleBooksApiResponse response) {
        return toResponseList(response, null);
    }

    public List<BookResponse> toResponseList(GoogleBooksApiResponse response, UserCollectionInfo userCollectionInfo) {
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
                            volumeInfo.infoLink(),
                            volumeInfo.previewLink(),
                            language,
                            volumeInfo.pageCount(),
                            volumeInfo.averageRating(),
                            volumeInfo.ratingsCount(),
                            userCollectionInfo
                    );
                })
                .toList();
    }
}
