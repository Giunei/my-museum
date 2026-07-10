package com.giunei.my_museum.preference.entity;

import lombok.Getter;

@Getter
public enum BookGenre {
    FANTASY("Fantasia"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Ficção Científica"),
    HORROR("Terror"),
    MYSTERY("Mistério"),
    DRAMA("Drama"),
    LGBTQIA_PLUS("LGBTQIA+"),

    THRILLER("Suspense"),
    ADVENTURE("Aventura"),
    ACTION("Ação"),
    HISTORICAL_FICTION("Ficção Histórica"),

    BIOGRAPHY("Biografia"),
    AUTOBIOGRAPHY("Autobiografia"),
    MEMOIR("Memórias"),

    SELF_HELP("Autoajuda"),
    PSYCHOLOGY("Psicologia"),
    PHILOSOPHY("Filosofia"),

    BUSINESS("Negócios"),
    FINANCE("Finanças"),
    PRODUCTIVITY("Produtividade"),

    HISTORY("História"),
    POLITICS("Política"),
    SCIENCE("Ciência"),

    POETRY("Poesia"),
    CLASSIC("Clássicos"),

    YOUNG_ADULT("Young Adult"),
    DYSTOPIAN("Distopia"),

    CRIME("Crime"),
    TRUE_CRIME("Crime Real"),

    RELIGION("Religião"),
    SPIRITUALITY("Espiritualidade"),

    COMICS("Quadrinhos"),
    MANGA("Mangá");

    private final String label;

    BookGenre(String label) {
        this.label = label;
    }
}
