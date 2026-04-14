package com.giunei.my_museum.features.highlight;

public enum Review {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5");

    private final String value;

    Review(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
