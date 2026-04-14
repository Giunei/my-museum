package com.giunei.my_museum.features.museum;

import com.giunei.my_museum.features.museum.dto.MuseumResponse;
import com.giunei.my_museum.features.highlight.Highlight;
import com.giunei.my_museum.features.highlight.dto.HighlightResponse;

import java.util.List;

public class MuseumMapper {

    public static MuseumResponse toResponse(Museum museum) {
        List<HighlightResponse> highlights = museum.getHighlights()
                .stream()
                .map(MuseumMapper::toHighlightResponse)
                .toList();

        return new MuseumResponse(
                museum.getId(),
                museum.getUser().getUsername(),
                highlights
        );
    }

    private static HighlightResponse toHighlightResponse(Highlight h) {
        return new HighlightResponse(
                h.getId(),
                h.getName(),
                Boolean.TRUE.equals(h.getFinished()),
                Boolean.TRUE.equals(h.getPlatinumed()),
                h.getCategory().getName()
        );
    }
}
