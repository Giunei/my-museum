package com.giunei.my_museum.media.controller;

import com.giunei.my_museum.media.dto.UpdateMediaResult;
import com.giunei.my_museum.media.dto.UpdateUserMediaRequest;
import com.giunei.my_museum.media.dto.UserMediaRequest;
import com.giunei.my_museum.media.dto.UserMediaResponse;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.UserMediaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Validated
public class UserMediaController {

    private final UserMediaService service;

    @PostMapping
    public UserMediaResponse create(@RequestBody @Valid UserMediaRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Page<UserMediaResponse> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) Boolean completed
    ) {
        return service.findAll(page, size, type, completed);
    }

    @GetMapping("/collection/{collectionId}")
    public Page<UserMediaResponse> findByCollection(
            @PathVariable Long collectionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return service.findByCollection(collectionId, page, size);
    }

    @PostMapping("/{id}/collection/{collectionId}")
    public void addToCollection(@PathVariable Long id, @PathVariable Long collectionId) {
        service.addToCollection(id, collectionId);
    }

    @DeleteMapping("/{id}/collection/{collectionId}")
    public void removeFromCollection(@PathVariable Long id, @PathVariable Long collectionId) {
        service.removeFromCollection(id, collectionId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/highlighted")
    public List<UserMediaResponse> getHighlighted(
            @RequestParam MediaType type
    ) {
        return service.getHighlighted(type);
    }

    @GetMapping("/wishlist")
    public List<UserMediaResponse> getWishlist(
            @RequestParam MediaType type
    ) {
        return service.getWishlist(type);
    }

    @PutMapping("/highlighted/order")
    public void updateOrder(@RequestBody List<Long> ids) {
        service.updateOrder(ids);
    }

    @PatchMapping("/{id}")
    public UpdateMediaResult updateMedia(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserMediaRequest request
    ) {
        return service.updateMedia(id, request);
    }
}