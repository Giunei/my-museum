package com.giunei.my_museum.features.user.preference.entity;

import lombok.Getter;

@Getter
public enum GameGenre {
    COOP("Coop"),
    TERROR("Terror"),
    MOBA("MOBA"),
    BATTLE_ROYALE("Battle Royale"),
    FPS("FPS"),
    RPG("RPG"),
    STRATEGY("Estratégia");

    private final String label;

    GameGenre(String label) {
        this.label = label;
    }
}
