package com.giunei.my_museum.features.user.profile.comment.dto;

import java.time.LocalDateTime;

public record ProfileCommentResponse(
        Long id,
        Long authorId,
        String authorName,
        String authorImage,
        String content,
        LocalDateTime createdAt
) {
}
