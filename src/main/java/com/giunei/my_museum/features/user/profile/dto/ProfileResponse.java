package com.giunei.my_museum.features.user.profile.dto;

public record ProfileResponse(
        UserInfo user,
        ProfileInfo profile,
        PersonInfo person,
        SocialInfo social,
        HighlightInfo highlights,
        GamificationInfo gamification
) {
}
