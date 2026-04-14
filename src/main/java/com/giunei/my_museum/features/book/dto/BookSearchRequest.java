package com.giunei.my_museum.features.book.dto;

import java.util.List;

public record BookSearchRequest(
        String query,
        List<String> genres,
        int page,
        int size
) {
}
