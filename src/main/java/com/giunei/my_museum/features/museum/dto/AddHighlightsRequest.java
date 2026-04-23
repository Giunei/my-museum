package com.giunei.my_museum.features.museum.dto;

import com.giunei.my_museum.features.highlight.dto.CategoryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record AddHighlightsRequest(
        @NotNull
        @Positive
        Long museumId,

        @NotEmpty
        List<@Valid CategoryRequest> categories
) {
}