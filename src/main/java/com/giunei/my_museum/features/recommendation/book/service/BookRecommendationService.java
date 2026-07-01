package com.giunei.my_museum.features.recommendation.book.service;

import com.giunei.my_museum.features.book.catalog.entity.BookCatalog;
import com.giunei.my_museum.features.book.catalog.service.BookCatalogService;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.recommendation.dto.RecommendationBucket;
import com.giunei.my_museum.features.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.features.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.model.RecommendationCatalogItem;
import com.giunei.my_museum.features.recommendation.service.AbstractRecommendationService;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.preference.entity.PreferenceType;
import com.giunei.my_museum.features.user.preference.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRecommendationService extends AbstractRecommendationService<BookCatalog> {

    private final BookCatalogService bookCatalogService;
    private final PreferenceRepository preferenceRepository;
    private final UserMediaRepository userMediaRepository;

    public RecommendationResponse recommendedForYou(User user, int limitPerBucket) {
        Set<String> preferredGenres = preferenceRepository.findByUser(user).stream()
                .filter(preference -> preference.getType() == PreferenceType.BOOK)
                .map(Preference::getValue)
                .filter(value -> value != null && !value.isBlank())
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<BookCatalog> catalog = loadCatalog();
        List<RecommendationItem> items = catalog.stream()
                .limit(100)
                .map(book -> toPreferenceItem(book, preferredGenres))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();

        return bucketize(items, limitPerBucket, limitPerBucket, limitPerBucket * 2);
    }

    public RecommendationResponse maybeYouLike(User user, int limitPerBucket) {
        List<BookCatalog> catalog = loadCatalog();
        Map<String, BookCatalog> catalogByTitle = catalog.stream()
                .collect(Collectors.toMap(book -> normalize(book.getTitle()), book -> book, (left, right) -> left, HashMap::new));

        List<UserMedia> likedBooks = userMediaRepository.findByUser(user, Pageable.unpaged()).stream()
                .filter(media -> media.getType() == MediaType.BOOK)
                .filter(media -> media.getRating() != null && media.getRating() >= 4)
                .toList();

        Map<String, Integer> authorWeights = new HashMap<>();
        Map<String, Integer> genreWeights = new HashMap<>();
        Set<String> consumedTitles = new LinkedHashSet<>();

        for (UserMedia media : likedBooks) {
            String titleKey = normalize(media.getTitle());
            consumedTitles.add(titleKey);

            BookCatalog matched = catalogByTitle.get(titleKey);
            if (matched == null) {
                continue;
            }

            int weight = media.getRating() == 5 ? 3 : 2;
            authorWeights.merge(normalize(matched.getAuthor()), weight, Integer::sum);

            for (String genre : matched.getGenres()) {
                genreWeights.merge(normalize(genre), media.getRating() == 5 ? 2 : 1, Integer::sum);
            }
        }

        List<RecommendationItem> items = catalog.stream()
                .limit(100)
                .filter(book -> !consumedTitles.contains(normalize(book.getTitle())))
                .map(book -> toSimilarItem(book, authorWeights, genreWeights))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();

        return bucketize(items, limitPerBucket, limitPerBucket, limitPerBucket * 2);
    }

    public List<BookCatalog> loadCatalog() {
        return bookCatalogService.findAll();
    }

    private RecommendationItem toPreferenceItem(BookCatalog book, Set<String> preferredGenres) {
        int matches = 0;

        for (String genre : book.getGenres()) {
            if (preferredGenres.contains(normalize(genre))) {
                matches++;
            }
        }

        if (matches == 0) {
            return toItem(book, 0);
        }

        int score = editorialWeight(book.getEditorialCategory()) + (matches * 12);

        if (matches >= 2) {
            score += 8;
        }

        if (matches >= 3) {
            score += 6;
        }

        return toItem(book, score);
    }

    private RecommendationItem toSimilarItem(BookCatalog book, Map<String, Integer> authorWeights, Map<String, Integer> genreWeights) {
        int score = editorialWeight(book.getEditorialCategory());

        score += authorWeights.getOrDefault(normalize(book.getAuthor()), 0) * 3;

        for (String genre : book.getGenres()) {
            score += genreWeights.getOrDefault(normalize(genre), 0) * 2;
        }

        return toItem(book, score);
    }

    private RecommendationResponse bucketize(List<RecommendationItem> items,
                                                 int trendingLimit,
                                                 int bestsellerLimit,
                                                 int classicLimit) {
        Map<EditorialCategory, List<RecommendationItem>> buckets = new EnumMap<>(EditorialCategory.class);

        buckets.put(EditorialCategory.TRENDING, filterByCategory(items, EditorialCategory.TRENDING, trendingLimit));
        buckets.put(EditorialCategory.BESTSELLER, filterByCategory(items, EditorialCategory.BESTSELLER, bestsellerLimit));
        buckets.put(EditorialCategory.CLASSIC, filterByCategory(items, EditorialCategory.CLASSIC, classicLimit));

        return new RecommendationResponse(List.of(
                new RecommendationBucket(EditorialCategory.TRENDING, buckets.get(EditorialCategory.TRENDING)),
                new RecommendationBucket(EditorialCategory.BESTSELLER, buckets.get(EditorialCategory.BESTSELLER)),
                new RecommendationBucket(EditorialCategory.CLASSIC, buckets.get(EditorialCategory.CLASSIC))
        ));
    }

    private List<RecommendationItem> filterByCategory(List<RecommendationItem> items,
                                                      EditorialCategory category,
                                                      int limit) {
        return items.stream()
                .filter(item -> item.editorialCategory() == category)
                .limit(limit)
                .toList();
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
