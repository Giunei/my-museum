package com.giunei.my_museum.book.dto;

public record FavoriteAuthorResponse(
        String author,
        int bookCount
) {
}
