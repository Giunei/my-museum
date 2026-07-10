package com.giunei.my_museum.profile.dto;

public record ProfileResponse(
        UserInfo user,
        ProfileInfo profile,
        PersonInfo person,
        SocialInfo social,
        Long totalItems,
        ProfileVisibilityInfo visibility
) {
}
