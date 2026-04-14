package com.giunei.my_museum.features.museum.dto;

import com.giunei.my_museum.features.highlight.dto.CategoryRequest;

import java.util.List;

public record AddHighlightsRequest(
        Long museumId,
        List<CategoryRequest> categories
) {
}