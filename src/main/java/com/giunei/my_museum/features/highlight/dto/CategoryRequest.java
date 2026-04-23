package com.giunei.my_museum.features.highlight.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CategoryRequest(
    Long id,
    @Size(max = 255)
    String name,
    @Size(max = 1000)
    String photo,
    @NotEmpty
    List<@Valid HighlightRequest> highlights)
{}
