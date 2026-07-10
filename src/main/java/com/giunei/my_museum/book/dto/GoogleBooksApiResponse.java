package com.giunei.my_museum.book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksApiResponse(
        Integer totalItems,
        List<Item> items
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String id,
            VolumeInfo volumeInfo
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(
            String title,
            List<String> authors,
            String description,
            ImageLinks imageLinks,
            String infoLink,
            String previewLink,
            String language,
            Integer pageCount,
            Double averageRating,
            Integer ratingsCount,
            List<String> categories
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageLinks(
            String thumbnail
    ) {
    }
}
