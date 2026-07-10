package com.giunei.my_museum.profile.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.common.exception.AccessDeniedException;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.entity.FollowStatus;
import com.giunei.my_museum.social.repository.FollowRepository;
import com.giunei.my_museum.profile.FollowRelationStatus;
import com.giunei.my_museum.profile.dto.ProfileVisibilityInfo;
import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileAccessService {

    private static final String PRIVATE_PROFILE_MESSAGE =
            "Este perfil é privado. Siga o usuário para ver o conteúdo do museu.";

    private final UserLookupService userLookupService;
    private final FollowRepository followRepository;

    public User requireUserWithFullProfileAccess(String username) {
        User target = userLookupService.requireByUsername(username);
        requireFullProfileAccess(target);
        return target;
    }

    public void requireFullProfileAccess(User target) {
        if (!canViewFullProfile(target, SecurityUtils.getAuthenticatedUserOrNull())) {
            throw new AccessDeniedException(PRIVATE_PROFILE_MESSAGE);
        }
    }

    public boolean canViewFullProfile(User target, User viewer) {
        if (isOwner(target, viewer)) {
            return true;
        }
        if (!isPrivateProfile(target)) {
            return true;
        }
        return viewer != null && hasAcceptedFollow(viewer, target);
    }

    public ProfileVisibilityInfo resolveVisibility(User target, User viewer) {
        FollowRelationStatus followStatus = resolveFollowRelationStatus(target, viewer);
        boolean canViewFullProfile = followStatus == FollowRelationStatus.SELF
                || followStatus == FollowRelationStatus.ACCEPTED
                || !isPrivateProfile(target);

        return new ProfileVisibilityInfo(
                isPrivateProfile(target),
                canViewFullProfile,
                followStatus
        );
    }

    public FollowRelationStatus resolveFollowRelationStatus(User target, User viewer) {
        if (isOwner(target, viewer)) {
            return FollowRelationStatus.SELF;
        }
        if (viewer == null) {
            return FollowRelationStatus.NONE;
        }

        return followRepository.findByFollowerAndFollowing(viewer, target)
                .map(follow -> follow.getStatus() == FollowStatus.ACCEPTED
                        ? FollowRelationStatus.ACCEPTED
                        : FollowRelationStatus.PENDING)
                .orElse(FollowRelationStatus.NONE);
    }

    public boolean isPrivateProfile(User target) {
        Profile profile = target.getProfile();
        return profile != null && profile.isPrivateProfile();
    }

    private boolean isOwner(User target, User viewer) {
        return viewer != null && target.getId().equals(viewer.getId());
    }

    private boolean hasAcceptedFollow(User follower, User following) {
        return followRepository.existsByFollowerAndFollowingAndStatus(
                follower,
                following,
                FollowStatus.ACCEPTED
        );
    }
}
