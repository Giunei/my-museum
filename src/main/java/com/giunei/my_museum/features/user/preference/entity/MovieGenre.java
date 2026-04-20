package com.giunei.my_museum.features.user.preference.entity;

import lombok.Getter;

@Getter
public enum MovieGenre {
    ACTION("Ação"),
    COMEDY("Comédia"),
    DRAMA("Drama"),
    HORROR("Terror"),
    THRILLER("Suspense"),
    SCI_FI("Ficção Científica");

    private final String label;

    MovieGenre(String label) {
        this.label = label;
    }

}
