package com.giunei.my_museum.features.user.profile.dto;

import com.giunei.my_museum.features.highlight.dto.HighlightResponse;

import java.util.List;

public record HighlightInfo(
        List<HighlightResponse> highlights
) {
}
