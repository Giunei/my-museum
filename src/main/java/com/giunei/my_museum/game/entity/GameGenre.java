package com.giunei.my_museum.game.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameGenre {

    RPG("RPG"),
    ACTION("Ação"),
    ADVENTURE("Aventura"),
    FPS("FPS"),
    HORROR("Terror"),
    MOBA("MOBA"),
    STRATEGY("Estratégia"),
    SURVIVAL("Sobrevivência"),
    SANDBOX("Sandbox"),
    PLATFORMER("Plataforma"),
    RACING("Corrida"),
    SPORTS("Esportes"),
    FIGHTING("Luta"),
    PUZZLE("Puzzle"),
    COOP("Cooperativo");

    private final String label;
}
