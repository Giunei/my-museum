package com.giunei.my_museum.features.user.profile.comment.dto;

public record ProfileCommentRequest(
        Long profileOwnerId,
        String content
) {
}
