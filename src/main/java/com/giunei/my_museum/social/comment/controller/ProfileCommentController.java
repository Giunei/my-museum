package com.giunei.my_museum.social.comment.controller;

import com.giunei.my_museum.social.comment.dto.CreateProfileCommentRequest;
import com.giunei.my_museum.social.comment.dto.ProfileCommentResponse;
import com.giunei.my_museum.social.comment.dto.UpdateProfileCommentRequest;
import com.giunei.my_museum.social.comment.service.ProfileCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/{username}/comments")
@RequiredArgsConstructor
public class ProfileCommentController {

    private final ProfileCommentService service;

    @GetMapping
    public Page<ProfileCommentResponse> list(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.findByUsername(username, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileCommentResponse create(
            @PathVariable String username,
            @RequestBody CreateProfileCommentRequest request
    ) {
        return service.createByUsername(username, request.content());
    }

    @PutMapping("/{commentId}")
    public ProfileCommentResponse update(
            @PathVariable String username,
            @PathVariable Long commentId,
            @RequestBody UpdateProfileCommentRequest request
    ) {
        return service.updateForProfile(username, commentId, request.content());
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String username,
            @PathVariable Long commentId
    ) {
        service.deleteForProfile(username, commentId);
    }
}
