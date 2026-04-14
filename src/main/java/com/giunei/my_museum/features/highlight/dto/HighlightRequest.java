package com.giunei.my_museum.features.highlight.dto;

public record HighlightRequest(
        String name,
        boolean finished,
        boolean platinumed
) {}
