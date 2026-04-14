package com.giunei.my_museum.features.museum.controller;

import com.giunei.my_museum.features.museum.MuseumService;
import com.giunei.my_museum.features.museum.dto.AddHighlightsRequest;
import com.giunei.my_museum.features.museum.dto.CreateMuseumRequest;
import com.giunei.my_museum.features.museum.dto.MuseumResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/museums")
@RequiredArgsConstructor
public class MuseumController {

    private final MuseumService service;

    @PostMapping
    public MuseumResponse save(@RequestBody CreateMuseumRequest museum) {
        return service.save(museum);
    }

    @GetMapping
    public List<MuseumResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MuseumResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/highlights")
    public MuseumResponse addHighlight(@RequestBody AddHighlightsRequest request) {
        return service.addHighlightSession(request);
    }
}
