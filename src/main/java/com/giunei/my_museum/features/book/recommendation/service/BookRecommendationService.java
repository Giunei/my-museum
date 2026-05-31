package com.giunei.my_museum.features.book.recommendation.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.book.catalog.entity.BookCatalog;
import com.giunei.my_museum.features.book.catalog.entity.BookEditorialCategory;
import com.giunei.my_museum.features.book.catalog.repository.BookCatalogRepository;
import com.giunei.my_museum.features.book.recommendation.dto.BookRecommendationBucket;
import com.giunei.my_museum.features.book.recommendation.dto.BookRecommendationItem;
import com.giunei.my_museum.features.book.recommendation.dto.BookRecommendationResponse;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.preference.entity.PreferenceType;
import com.giunei.my_museum.features.user.preference.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRecommendationService {

    private final BookCatalogRepository bookCatalogRepository;
    private final PreferenceRepository preferenceRepository;
    private final UserMediaRepository userMediaRepository;

    public BookRecommendationResponse recommendedForYou(int limitPerBucket) {
        User user = SecurityUtils.getAuthenticatedUser();
        Set<String> preferredGenres = preferenceRepository.findByUser(user).stream()
                .filter(preference -> preference.getType() == PreferenceType.BOOK)
                .map(Preference::getValue)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<BookRecommendationItem> items = bookCatalogRepository.findAll().stream()
                .map(book -> toItem(book, scoreForPreferences(book, preferredGenres)))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();

        return bucketize(items, limitPerBucket);
    }

    public BookRecommendationResponse maybeYouLike(int limitPerBucket) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<UserMedia> likedBooks = userMediaRepository.findByUser(user, org.springframework.data.domain.Pageable.unpaged()).stream()
                .filter(media -> media.getType() != null && media.getType().name().equalsIgnoreCase("BOOK"))
                .filter(media -> media.getRating() != null && media.getRating() >= 4)
                .toList();

        Map<String, Integer> authorWeight = new LinkedHashMap<>();
        Map<String, Integer> genreWeight = new LinkedHashMap<>();

        for (UserMedia media : likedBooks) {
            String title = normalize(media.getTitle());
            for (BookCatalog book : bookCatalogRepository.findAll()) {
                if (normalize(book.getTitle()).equals(title)) {
                    authorWeight.merge(normalize(book.getAuthor()), media.getRating() == 5 ? 3 : 2, Integer::sum);
                    for (String genre : book.getGenres()) {
                        genreWeight.merge(normalize(genre), media.getRating() == 5 ? 2 : 1, Integer::sum);
                    }
                }
            }
        }

        Set<String> consumedTitles = likedBooks.stream()
                .map(UserMedia::getTitle)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<BookRecommendationItem> items = bookCatalogRepository.findAll().stream()
                .filter(book -> !consumedTitles.contains(normalize(book.getTitle())))
                .map(book -> toItem(book, scoreForSimilarBooks(book, authorWeight, genreWeight)))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();

        return bucketize(items, limitPerBucket);
    }

    private int scoreForPreferences(BookCatalog book, Set<String> preferredGenres) {
        int score = editorialWeight(book.getEditorialCategory());

        for (String genre : book.getGenres()) {
            if (preferredGenres.contains(normalize(genre))) {
                score += 10;
            }
        }

        score += Math.min(book.getGenres().size(), 3);
        return score;
    }

    private int scoreForSimilarBooks(BookCatalog book, Map<String, Integer> authorWeight, Map<String, Integer> genreWeight) {
        int score = editorialWeight(book.getEditorialCategory());

        score += authorWeight.getOrDefault(normalize(book.getAuthor()), 0) * 3;

        for (String genre : book.getGenres()) {
            score += genreWeight.getOrDefault(normalize(genre), 0) * 2;
        }

        return score;
    }

    private int editorialWeight(BookEditorialCategory category) {
        return switch (category) {
            case TRENDING -> 30;
            case BESTSELLER -> 20;
            case CLASSIC -> 10;
        };
    }

    private BookRecommendationItem toItem(BookCatalog book, int score) {
        return new BookRecommendationItem(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenres(),
                book.getEditorialCategory(),
                score
        );
    }

    private Comparator<BookRecommendationItem> recommendationComparator() {
        return Comparator
                .comparingInt(BookRecommendationItem::score).reversed()
                .thenComparing(item -> item.editorialCategory() == BookEditorialCategory.TRENDING ? 0 :
                        item.editorialCategory() == BookEditorialCategory.BESTSELLER ? 1 : 2)
                .thenComparing(BookRecommendationItem::title, String.CASE_INSENSITIVE_ORDER);
    }

    private BookRecommendationResponse bucketize(List<BookRecommendationItem> items, int limitPerBucket) {
        Map<BookEditorialCategory, List<BookRecommendationItem>> byCategory = new EnumMap<>(BookEditorialCategory.class);

        for (BookEditorialCategory category : BookEditorialCategory.values()) {
            List<BookRecommendationItem> bucket = items.stream()
                    .filter(item -> item.editorialCategory() == category)
                    .limit(limitPerBucket)
                    .toList();
            byCategory.put(category, bucket);
        }

        return new BookRecommendationResponse(byCategory.entrySet().stream()
                .map(entry -> new BookRecommendationBucket(entry.getKey(), entry.getValue()))
                .toList());
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
