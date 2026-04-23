package com.giunei.my_museum.features.museum.controller;

import com.giunei.my_museum.features.museum.MuseumService;
import com.giunei.my_museum.features.museum.dto.AddHighlightsRequest;
import com.giunei.my_museum.features.museum.dto.CreateMuseumRequest;
import com.giunei.my_museum.features.museum.dto.MuseumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/museums")
@RequiredArgsConstructor
public class MuseumController {

    private final MuseumService service;

    @PostMapping
    public MuseumResponse save(@RequestBody @Valid CreateMuseumRequest museum) {
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
    public MuseumResponse addHighlight(@RequestBody @Valid AddHighlightsRequest request) {
        return service.addHighlightSession(request);
    }
}
