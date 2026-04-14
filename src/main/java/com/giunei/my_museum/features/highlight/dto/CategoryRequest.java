package com.giunei.my_museum.features.highlight.dto;

import java.util.List;

public record CategoryRequest(
    Long id,
    String name,
    String photo,
    List<HighlightRequest> highlights)
{}
