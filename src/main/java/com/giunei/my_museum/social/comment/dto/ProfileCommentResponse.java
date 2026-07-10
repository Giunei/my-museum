package com.giunei.my_museum.social.comment.dto;

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
