package com.giunei.my_museum.profile.dto;

import com.giunei.my_museum.profile.FollowRelationStatus;

public record ProfileVisibilityInfo(
        boolean privateProfile,
        boolean canViewFullProfile,
        FollowRelationStatus followStatus
) {
}
