package com.giunei.my_museum.features.lol.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LolPlatform {
    BR1("br1"),
    NA1("na1"),
    LA1("la1"),
    LA2("la2"),
    OC1("oc1"),
    EUW1("euw1"),
    EUN1("eun1"),
    TR1("tr1"),
    RU("ru"),
    KR("kr"),
    JP1("jp1");

    private final String value;

    LolPlatform(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LolPlatform fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Platform is required");
        }
        for (LolPlatform platform : values()) {
            if (platform.value.equalsIgnoreCase(value) || platform.name().equalsIgnoreCase(value)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("Invalid platform: " + value);
    }

    public String routingRegion() {
        return switch (this) {
            case BR1, NA1, LA1, LA2, OC1 -> "americas";
            case EUW1, EUN1, TR1, RU -> "europe";
            case KR, JP1 -> "asia";
        };
    }
}
