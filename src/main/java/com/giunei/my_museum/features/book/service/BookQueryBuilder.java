package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.features.book.dto.BookSearchRequest;
import org.springframework.stereotype.Component;

@Component
public class BookQueryBuilder {

    public String build(BookSearchRequest request) {
        StringBuilder query = new StringBuilder();

        if (request.query() != null && !request.query().isBlank()) {
            query.append(request.query()).append(" ");
        }

        if (request.genres() != null) {
            request.genres().forEach(g ->
                    query.append("subject:").append(g).append(" ")
            );
        }

        return query.toString().trim();
    }
}
