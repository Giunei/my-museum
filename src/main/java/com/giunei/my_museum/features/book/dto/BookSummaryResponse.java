package com.giunei.my_museum.features.book.dto;

public record BookSummaryResponse(
        int totalBooks,
        int booksRead,
        Integer totalPagesRead
) {
}
