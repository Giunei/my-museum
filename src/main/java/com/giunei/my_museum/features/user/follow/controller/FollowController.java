package com.giunei.my_museum.features.user.follow.controller;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{username}")
    public ResponseEntity<Void> follow(@PathVariable String username) {
        User follower = SecurityUtils.getAuthenticatedUser();
        followService.follow(follower, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> unfollow(@PathVariable String username) {
        User follower = SecurityUtils.getAuthenticatedUser();
        followService.unfollow(follower, username);
        return ResponseEntity.noContent().build();
    }
}
