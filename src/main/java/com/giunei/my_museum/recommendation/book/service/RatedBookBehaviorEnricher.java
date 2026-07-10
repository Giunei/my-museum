package com.giunei.my_museum.recommendation.book.service;

import com.giunei.my_museum.book.dto.BookVolumeSnapshot;
import com.giunei.my_museum.book.service.CachedBookVolumeService;
import com.giunei.my_museum.book.service.GoogleBooksGenreMapper;
import com.giunei.my_museum.media.entity.UserMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RatedBookBehaviorEnricher {

    private final CachedBookVolumeService cachedBookVolumeService;
    private final GoogleBooksGenreMapper genreMapper;

    public void enrich(
            UserMedia media,
            int creatorWeight,
            int genreWeight,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights
    ) {
        if (media.getExternalId() == null || media.getExternalId().isBlank()) {
            return;
        }

        BookVolumeSnapshot snapshot = cachedBookVolumeService.getSnapshot(media.getExternalId());
        if (snapshot == null) {
            return;
        }

        for (String author : snapshot.authors()) {
            if (author != null && !author.isBlank()) {
                creatorWeights.merge(normalize(author), creatorWeight, Integer::sum);
            }
        }

        Set<String> genres = genreMapper.mapCategories(snapshot.categories());
        for (String genre : genres) {
            genreWeights.merge(normalize(genre), genreWeight, Integer::sum);
        }
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
