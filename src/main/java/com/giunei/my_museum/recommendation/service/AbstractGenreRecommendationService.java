package com.giunei.my_museum.recommendation.service;

import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.recommendation.dto.MaybeYouLikeResult;
import com.giunei.my_museum.recommendation.dto.RecommendationItem;
import com.giunei.my_museum.recommendation.dto.RecommendationResponse;
import com.giunei.my_museum.recommendation.model.RecommendationCatalogItem;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.preference.entity.Preference;
import com.giunei.my_museum.preference.entity.PreferenceType;
import com.giunei.my_museum.preference.repository.PreferenceRepository;
import org.springframework.data.domain.Pageable;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractGenreRecommendationService<T extends RecommendationCatalogItem>
        extends AbstractRecommendationService<T> {

    public static final int MIN_RATING_FOR_SIGNAL = 4;
    public static final int MIN_RATED_ITEMS_FOR_DISCOVERY = 3;

    private static final int DISCOVERY_EDITORIAL_CAP = 8;
    private static final int DISCOVERY_GENRE_MULTIPLIER = 4;
    private static final int DISCOVERY_CREATOR_MULTIPLIER = 5;
    private static final int DISCOVERY_EXPLORATION_BONUS = 12;
    private static final int MIN_PARTIAL_TITLE_LENGTH = 8;

    private final PreferenceRepository preferenceRepository;
    private final UserMediaRepository userMediaRepository;

    protected AbstractGenreRecommendationService(
            PreferenceRepository preferenceRepository,
            UserMediaRepository userMediaRepository
    ) {
        this.preferenceRepository = preferenceRepository;
        this.userMediaRepository = userMediaRepository;
    }

    protected RecommendationResponse recommendedForYou(
            User user,
            PreferenceType preferenceType,
            int limitPerBucket,
            List<T> catalog
    ) {
        Set<String> preferredGenres = loadOnboardingGenres(user, preferenceType);

        if (preferredGenres.isEmpty()) {
            return bucketize(List.of(), limitPerBucket, limitPerBucket, limitPerBucket * 2);
        }

        List<RecommendationItem> items = catalog.stream()
                .map(item -> toPreferenceItem(item, preferredGenres))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();

        return bucketize(items, limitPerBucket, limitPerBucket, limitPerBucket * 2);
    }

    protected MaybeYouLikeResult maybeYouLike(
            User user,
            MediaType mediaType,
            PreferenceType preferenceType,
            int limitPerBucket,
            List<T> catalog,
            Set<Long> excludeIds
    ) {
        Map<String, T> catalogByTitle = catalog.stream()
                .collect(Collectors.toMap(
                        item -> normalize(item.getTitle()),
                        item -> item,
                        (left, right) -> left,
                        HashMap::new
                ));

        List<UserMedia> likedItems = userMediaRepository.findByUser(user, Pageable.unpaged()).stream()
                .filter(media -> media.getType() == mediaType)
                .filter(media -> media.getRating() != null && media.getRating() >= MIN_RATING_FOR_SIGNAL)
                .toList();

        if (likedItems.size() < MIN_RATED_ITEMS_FOR_DISCOVERY) {
            return MaybeYouLikeResult.unavailable(likedItems.size(), MIN_RATED_ITEMS_FOR_DISCOVERY);
        }

        Set<String> onboardingGenres = loadOnboardingGenres(user, preferenceType);

        Map<String, Integer> creatorWeights = new HashMap<>();
        Map<String, Integer> genreWeights = new HashMap<>();
        Set<String> consumedTitles = new LinkedHashSet<>();

        for (UserMedia media : likedItems) {
            String titleKey = normalize(media.getTitle());
            consumedTitles.add(titleKey);

            int weight = media.getRating() == 5 ? 3 : 2;
            int genreWeight = media.getRating() == 5 ? 2 : 1;

            T matched = resolveCatalogMatch(catalog, catalogByTitle, media);
            if (matched != null) {
                absorbCatalogSignals(matched, weight, genreWeight, creatorWeights, genreWeights);
            }

            absorbMediaAuthorSignals(media, catalog, weight, genreWeight, creatorWeights, genreWeights);
            enrichBehaviorFromRatedMedia(
                    media,
                    mediaType,
                    weight,
                    genreWeight,
                    creatorWeights,
                    genreWeights
            );
        }

        Set<Long> excluded = excludeIds == null ? Set.of() : excludeIds;

        List<RecommendationItem> items = rankDiscoveryItems(
                catalog,
                consumedTitles,
                excluded,
                creatorWeights,
                genreWeights,
                onboardingGenres
        );

        RecommendationResponse response = bucketize(items, limitPerBucket, limitPerBucket, limitPerBucket * 2);
        return MaybeYouLikeResult.available(likedItems.size(), MIN_RATED_ITEMS_FOR_DISCOVERY, response);
    }

    private List<RecommendationItem> rankDiscoveryItems(
            List<T> catalog,
            Set<String> consumedTitles,
            Set<Long> excluded,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights,
            Set<String> onboardingGenres
    ) {
        return catalog.stream()
                .filter(item -> !consumedTitles.contains(normalize(item.getTitle())))
                .filter(item -> item.getId() == null || !excluded.contains(item.getId()))
                .map(item -> toDiscoveryItem(item, creatorWeights, genreWeights, onboardingGenres))
                .filter(item -> item.score() > 0)
                .sorted(recommendationComparator())
                .toList();
    }

    private T resolveCatalogMatch(List<T> catalog, Map<String, T> catalogByTitle, UserMedia media) {
        String titleKey = normalize(media.getTitle());
        if (!titleKey.isBlank()) {
            T exact = catalogByTitle.get(titleKey);
            if (exact != null) {
                return exact;
            }

            for (T item : catalog) {
                String catalogTitle = normalize(item.getTitle());
                if (catalogTitle.equals(titleKey)) {
                    return item;
                }
                if (isPartialTitleMatch(titleKey, catalogTitle)) {
                    return item;
                }
            }
        }

        String authorKey = normalize(media.getAuthor());
        if (authorKey.isBlank()) {
            return null;
        }

        return catalog.stream()
                .filter(item -> authorKey.equals(normalize(item.getCreator())))
                .findFirst()
                .orElse(null);
    }

    private boolean isPartialTitleMatch(String left, String right) {
        if (left.length() < MIN_PARTIAL_TITLE_LENGTH || right.length() < MIN_PARTIAL_TITLE_LENGTH) {
            return false;
        }
        return left.contains(right) || right.contains(left);
    }

    private void absorbCatalogSignals(
            T matched,
            int creatorWeight,
            int genreWeight,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights
    ) {
        String creator = matched.getCreator();
        if (creator != null && !creator.isBlank()) {
            creatorWeights.merge(normalize(creator), creatorWeight, Integer::sum);
        }

        for (String genre : matched.getGenres()) {
            genreWeights.merge(normalize(genre), genreWeight, Integer::sum);
        }
    }

    private void absorbMediaAuthorSignals(
            UserMedia media,
            List<T> catalog,
            int creatorWeight,
            int genreWeight,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights
    ) {
        String author = media.getAuthor();
        if (author == null || author.isBlank()) {
            return;
        }

        String authorKey = normalize(author);
        creatorWeights.merge(authorKey, creatorWeight, Integer::sum);

        catalog.stream()
                .filter(item -> authorKey.equals(normalize(item.getCreator())))
                .flatMap(item -> item.getGenres().stream())
                .forEach(genre -> genreWeights.merge(normalize(genre), genreWeight, Integer::sum));
    }

    private Set<String> loadOnboardingGenres(User user, PreferenceType preferenceType) {
        return preferenceRepository.findByUser(user).stream()
                .filter(preference -> preference.getType() == preferenceType)
                .map(Preference::getValue)
                .filter(value -> value != null && !value.isBlank())
                .map(this::normalize)
                .collect(Collectors.toSet());
    }

    private RecommendationItem toPreferenceItem(T item, Set<String> preferredGenres) {
        int matches = 0;

        for (String genre : item.getGenres()) {
            if (preferredGenres.contains(normalize(genre))) {
                matches++;
            }
        }

        if (matches == 0) {
            return toItem(item, 0);
        }

        int score = editorialWeight(item.getEditorialCategory()) + (matches * 12);

        if (matches >= 2) {
            score += 8;
        }

        if (matches >= 3) {
            score += 6;
        }

        return toItem(item, score);
    }

    private RecommendationItem toDiscoveryItem(
            T item,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights,
            Set<String> onboardingGenres
    ) {
        int score = Math.min(editorialWeight(item.getEditorialCategory()), DISCOVERY_EDITORIAL_CAP);
        boolean hasBehaviorSignal = false;

        String creator = item.getCreator();
        if (creator != null && !creator.isBlank()) {
            int creatorScore = creatorWeights.getOrDefault(normalize(creator), 0);
            if (creatorScore > 0) {
                hasBehaviorSignal = true;
                score += creatorScore * DISCOVERY_CREATOR_MULTIPLIER;
            }
        }

        for (String genre : item.getGenres()) {
            String normalizedGenre = normalize(genre);
            int genreScore = genreWeights.getOrDefault(normalizedGenre, 0);
            if (genreScore > 0) {
                hasBehaviorSignal = true;
                score += genreScore * DISCOVERY_GENRE_MULTIPLIER;

                if (!onboardingGenres.contains(normalizedGenre)) {
                    score += DISCOVERY_EXPLORATION_BONUS;
                }
            }
        }

        if (!hasBehaviorSignal) {
            return toItem(item, 0);
        }

        return toItem(item, score);
    }

    protected void enrichBehaviorFromRatedMedia(
            UserMedia media,
            MediaType mediaType,
            int creatorWeight,
            int genreWeight,
            Map<String, Integer> creatorWeights,
            Map<String, Integer> genreWeights
    ) {
    }

    protected String normalize(String value) {
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
