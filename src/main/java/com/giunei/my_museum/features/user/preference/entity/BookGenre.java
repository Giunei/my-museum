package com.giunei.my_museum.features.user.preference.entity;

import lombok.Getter;

@Getter
public enum BookGenre {
    FANTASY("Fantasia"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Ficção Científica"),
    HORROR("Terror"),
    MYSTERY("Mistério"),
    DRAMA("Drama");

    private final String label;

    BookGenre(String label) {
        this.label = label;
    }
}
