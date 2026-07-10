package com.giunei.my_museum.book.dto;

public record BookSummaryResponse(
        int totalBooks,
        int booksRead,
        Integer totalPagesRead
) {
}
