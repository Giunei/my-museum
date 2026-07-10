package com.giunei.my_museum.recommendation.book.provider;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.book.client.GoogleBooksClient;
import com.giunei.my_museum.book.dto.BookResponse;
import com.giunei.my_museum.book.mapper.BookMapper;
import com.giunei.my_museum.recommendation.provider.RecommendationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookRecommendationProvider
        implements RecommendationProvider<BookResponse> {

    private final GoogleBooksClient googleBooksClient;
    private final BookMapper bookMapper;

    @Override
    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "books:recommendation-detail", key = "#title + '::' + #creator", unless = "#result == null")
    public BookResponse fetch(String title, String creator) {
        String query = "intitle:\"" + title + "\" inauthor:\"" + creator + "\"";
        try {
            var response = googleBooksClient.searchBooks(query, 0, 1, null, null);
            if (response == null || response.items() == null || response.items().isEmpty()) {
                return null;
            }
            return bookMapper.toResponse(response.items().get(0));
        } catch (Exception ignored) {
            return null;
        }
    }
}
