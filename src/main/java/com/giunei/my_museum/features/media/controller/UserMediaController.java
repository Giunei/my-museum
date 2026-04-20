package com.giunei.my_museum.features.media.controller;

import com.giunei.my_museum.features.media.dto.UserMediaRequest;
import com.giunei.my_museum.features.media.dto.UserMediaResponse;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.service.UserMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class UserMediaController {

    private final UserMediaService service;

    @PostMapping
    public UserMediaResponse create(@RequestBody UserMediaRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Page<UserMediaResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) Boolean completed
    ) {
        return service.findAll(page, size, type, completed);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}