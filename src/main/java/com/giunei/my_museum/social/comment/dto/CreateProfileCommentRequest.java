package com.giunei.my_museum.social.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateProfileCommentRequest(
        String content
) {
}
