package com.giunei.my_museum.features.book.dto;

import java.util.List;

public record BookResponse(
        String id,
        String title,
        List<String> authors,
        String description,
        String thumbnail,
        String language,
        Integer pageCount
) {
}
