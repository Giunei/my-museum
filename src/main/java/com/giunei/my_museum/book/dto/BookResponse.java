package com.giunei.my_museum.book.dto;

import com.giunei.my_museum.media.dto.UserCollectionInfo;

import java.util.List;

public record BookResponse(
        String id,
        String title,
        List<String> authors,
        String description,
        String thumbnail,
        String infoLink,
        String previewLink,
        String language,
        Integer pageCount,
        Double averageRating,
        Integer ratingsCount,
        UserCollectionInfo userCollectionInfo
) {
}
