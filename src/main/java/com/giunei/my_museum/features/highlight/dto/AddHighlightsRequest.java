package com.giunei.my_museum.features.highlight.dto;

import java.util.List;

public record AddHighlightsRequest(
        Long museumId,
        List<CategoryRequest> highlights
) {
}
