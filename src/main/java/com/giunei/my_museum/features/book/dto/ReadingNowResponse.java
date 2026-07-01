package com.giunei.my_museum.features.book.dto;

public record ReadingNowResponse(
        Long id,
        String title,
        String thumbnail,
        Integer pageCount
) {
}
