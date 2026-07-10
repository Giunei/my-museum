package com.giunei.my_museum.media.controller;

import com.giunei.my_museum.media.dto.MediaCollectionRequest;
import com.giunei.my_museum.media.dto.MediaCollectionResponse;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.MediaCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class MediaCollectionController {

    private final MediaCollectionService service;

    @GetMapping
    public List<MediaCollectionResponse> getCollectionsByType(@RequestParam MediaType type) {
        return service.getCollectionsByType(type);
    }

    @PostMapping
    public MediaCollectionResponse createCollection(@RequestBody MediaCollectionRequest request) {
        return service.createCollection(request);
    }

    @DeleteMapping("/{id}")
    public void deleteCollection(@PathVariable Long id) {
        service.deleteCollection(id);
    }
}
