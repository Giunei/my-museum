package com.giunei.my_museum.features.highlight;

import com.giunei.my_museum.features.highlight.repository.HighlightRepository;
import org.springframework.stereotype.Service;

@Service
public class HighlightService {

    private final HighlightRepository repository;

    public HighlightService(HighlightRepository repository) {
        this.repository = repository;
    }

    public Highlight save(Highlight highlight) {
        return repository.save(highlight);
    }
}
