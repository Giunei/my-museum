package com.giunei.my_museum.features.book.dto;

public record FavoriteAuthorResponse(
        String author,
        int bookCount
) {
}
