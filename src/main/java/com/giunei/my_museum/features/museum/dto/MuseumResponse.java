package com.giunei.my_museum.features.museum.dto;

import com.giunei.my_museum.features.highlight.dto.HighlightResponse;

import java.util.List;

public record MuseumResponse(
        Long id,
        String username,
        List<HighlightResponse> highlights
) {
}
