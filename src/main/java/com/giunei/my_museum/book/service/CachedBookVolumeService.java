package com.giunei.my_museum.book.service;

import com.giunei.my_museum.common.config.CacheManagers;
import com.giunei.my_museum.book.client.GoogleBooksClient;
import com.giunei.my_museum.book.dto.BookVolumeSnapshot;
import com.giunei.my_museum.book.dto.GoogleBooksApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CachedBookVolumeService {

    private final GoogleBooksClient googleBooksClient;

    @Cacheable(cacheManager = CacheManagers.RECOMMENDATION, value = "books:volume-snapshot", key = "#volumeId", unless = "#result == null")
    public BookVolumeSnapshot getSnapshot(String volumeId) {
        GoogleBooksApiResponse.Item item = googleBooksClient.getVolumeById(volumeId);
        if (item == null || item.volumeInfo() == null) {
            return null;
        }

        GoogleBooksApiResponse.VolumeInfo volumeInfo = item.volumeInfo();
        List<String> authors = volumeInfo.authors() != null ? volumeInfo.authors() : List.of();
        List<String> categories = volumeInfo.categories() != null ? volumeInfo.categories() : List.of();

        return new BookVolumeSnapshot(authors, categories);
    }
}
