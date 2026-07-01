package com.giunei.my_museum.features.user.profile.comment.controller;

import com.giunei.my_museum.features.user.profile.comment.dto.ProfileCommentRequest;
import com.giunei.my_museum.features.user.profile.comment.dto.ProfileCommentResponse;
import com.giunei.my_museum.features.user.profile.comment.service.ProfileCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile-comments")
@RequiredArgsConstructor
public class ProfileCommentController {

    private final ProfileCommentService service;

    @PostMapping
    public ProfileCommentResponse create(
            @RequestBody ProfileCommentRequest request
    ) {
        return service.create(request);
    }

    @GetMapping("/{userId}")
    public Page<ProfileCommentResponse> findByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.findByUser(userId, page, size);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
