package com.giunei.my_museum.social.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.dto.FollowRequestResponse;
import com.giunei.my_museum.social.dto.FollowStatusResponse;
import com.giunei.my_museum.social.service.FollowService;
import com.giunei.my_museum.profile.FollowRelationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/requests")
    public List<FollowRequestResponse> pendingRequests() {
        User profileOwner = SecurityUtils.getAuthenticatedUser();
        return followService.listPendingRequests(profileOwner);
    }

    @PostMapping("/requests/{username}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable String username) {
        User profileOwner = SecurityUtils.getAuthenticatedUser();
        followService.acceptFollowRequest(profileOwner, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/requests/{username}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable String username) {
        User profileOwner = SecurityUtils.getAuthenticatedUser();
        followService.rejectFollowRequest(profileOwner, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{username}")
    public FollowStatusResponse follow(@PathVariable String username) {
        User follower = SecurityUtils.getAuthenticatedUser();
        FollowRelationStatus status = followService.follow(follower, username);
        return new FollowStatusResponse(status);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> unfollow(@PathVariable String username) {
        User follower = SecurityUtils.getAuthenticatedUser();
        followService.unfollow(follower, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/status")
    public FollowStatusResponse followStatus(@PathVariable String username) {
        User follower = SecurityUtils.getAuthenticatedUser();
        FollowRelationStatus status = followService.getFollowRelationStatus(follower, username);
        return new FollowStatusResponse(status);
    }
}
