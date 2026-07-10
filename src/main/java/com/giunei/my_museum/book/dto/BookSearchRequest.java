package com.giunei.my_museum.book.dto;

import java.util.List;

public record BookSearchRequest(
        String query,
        String title,
        String author,
        String language,
        BookSearchSort sort,
        List<String> genres,
        int page,
        int size
) {
}
