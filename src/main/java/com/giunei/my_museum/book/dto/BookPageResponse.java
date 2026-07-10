package com.giunei.my_museum.book.dto;

import java.util.List;

public record BookPageResponse(
        List<BookResponse> content,
        int page,
        int size,
        boolean hasNext
) {
}
