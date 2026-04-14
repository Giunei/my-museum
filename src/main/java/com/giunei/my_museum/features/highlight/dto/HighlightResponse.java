package com.giunei.my_museum.features.highlight.dto;

public record HighlightResponse(
        Long id,
        String name,
        boolean finished,
        boolean platinumed,
        String categoryName
) {}
