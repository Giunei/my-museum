package com.giunei.my_museum.social.dto;

public record FollowRequestResponse(
        Long userId,
        String username,
        String profileImageUrl
) {
}
