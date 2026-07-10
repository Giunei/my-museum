package com.giunei.my_museum.book.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class GoogleBooksGenreMapper {

    public Set<String> mapCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return Set.of();
        }

        Set<String> genres = new LinkedHashSet<>();
        for (String category : categories) {
            genres.addAll(mapCategory(category));
        }
        return genres;
    }

    private Set<String> mapCategory(String category) {
        String normalized = normalize(category);
        if (normalized.isBlank()) {
            return Set.of();
        }

        Set<String> genres = new LinkedHashSet<>();

        if (containsAny(normalized, "fantasy", "magia", "magic")) {
            genres.add("FANTASY");
        }
        if (containsAny(normalized, "romance", "romantic", "love stories")) {
            genres.add("ROMANCE");
        }
        if (containsAny(normalized, "science fiction", "sci-fi", "ficcao cientifica")) {
            genres.add("SCIENCE_FICTION");
        }
        if (containsAny(normalized, "horror", "terror")) {
            genres.add("HORROR");
        }
        if (containsAny(normalized, "mystery", "mistery", "detective", "crime fiction")) {
            genres.add("MYSTERY");
        }
        if (containsAny(normalized, "thriller", "suspense")) {
            genres.add("THRILLER");
        }
        if (containsAny(normalized, "young adult", "juvenile", "teen", "ya ")) {
            genres.add("YOUNG_ADULT");
        }
        if (containsAny(normalized, "lgbt", "gay", "lesbian", "queer")) {
            genres.add("LGBTQIA_PLUS");
        }
        if (containsAny(normalized, "historical")) {
            genres.add("HISTORICAL_FICTION");
        }
        if (containsAny(normalized, "dystop")) {
            genres.add("DYSTOPIAN");
        }
        if (containsAny(normalized, "adventure")) {
            genres.add("ADVENTURE");
        }
        if (containsAny(normalized, "action")) {
            genres.add("ACTION");
        }
        if (containsAny(normalized, "biograph")) {
            genres.add("BIOGRAPHY");
        }
        if (containsAny(normalized, "self-help", "self help", "personal growth")) {
            genres.add("SELF_HELP");
        }
        if (containsAny(normalized, "psycholog")) {
            genres.add("PSYCHOLOGY");
        }
        if (containsAny(normalized, "philosoph")) {
            genres.add("PHILOSOPHY");
        }
        if (containsAny(normalized, "business", "economics", "finance")) {
            genres.add("BUSINESS");
        }
        if (containsAny(normalized, "history", "historia")) {
            genres.add("HISTORY");
        }
        if (containsAny(normalized, "politic")) {
            genres.add("POLITICS");
        }
        if (containsAny(normalized, "science", "ciencia")) {
            genres.add("SCIENCE");
        }
        if (containsAny(normalized, "poetry", "poesia")) {
            genres.add("POETRY");
        }
        if (containsAny(normalized, "classic")) {
            genres.add("CLASSIC");
        }
        if (containsAny(normalized, "manga")) {
            genres.add("MANGA");
        }
        if (containsAny(normalized, "comic", "graphic novel")) {
            genres.add("COMICS");
        }
        if (containsAny(normalized, "drama")) {
            genres.add("DRAMA");
        }
        if (containsAny(normalized, "religion", "spiritual")) {
            genres.add("SPIRITUALITY");
        }
        if (containsAny(normalized, "true crime")) {
            genres.add("TRUE_CRIME");
        } else if (containsAny(normalized, "crime")) {
            genres.add("CRIME");
        }

        if (genres.isEmpty() && containsAny(normalized, "fiction", "ficcao")) {
            genres.add("DRAMA");
        }

        return genres;
    }

    private boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
