package com.giunei.my_museum.features.highlight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HighlightRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        boolean finished,
        boolean platinumed
) {}
