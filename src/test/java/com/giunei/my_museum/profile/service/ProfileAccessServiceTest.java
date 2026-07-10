package com.giunei.my_museum.profile.service;

import com.giunei.my_museum.common.exception.AccessDeniedException;
import com.giunei.my_museum.profile.FollowRelationStatus;
import com.giunei.my_museum.social.entity.Follow;
import com.giunei.my_museum.social.entity.FollowStatus;
import com.giunei.my_museum.social.repository.FollowRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.service.UserLookupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ProfileAccessServiceTest extends AbstractUnitTest {

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private ProfileAccessService profileAccessService;

    @Test
    void should_allowFullAccess_when_viewerIsOwner() {
        var owner = TestFixtures.userWithProfile(1L, "owner", true);

        assertThat(profileAccessService.canViewFullProfile(owner, owner)).isTrue();
        assertThat(profileAccessService.resolveFollowRelationStatus(owner, owner))
                .isEqualTo(FollowRelationStatus.SELF);
    }

    @Test
    void should_allowFullAccess_when_profileIsPublic() {
        var owner = TestFixtures.userWithProfile(1L, "owner", false);
        var viewer = TestFixtures.user(2L, "viewer");

        assertThat(profileAccessService.canViewFullProfile(owner, viewer)).isTrue();
    }

    @Test
    void should_denyFullAccess_when_profileIsPrivateAndViewerIsAnonymous() {
        var owner = TestFixtures.userWithProfile(1L, "owner", true);

        assertThat(profileAccessService.canViewFullProfile(owner, null)).isFalse();
        assertThat(profileAccessService.resolveFollowRelationStatus(owner, null))
                .isEqualTo(FollowRelationStatus.NONE);
    }

    @Test
    void should_allowFullAccess_when_viewerHasAcceptedFollow() {
        var owner = TestFixtures.userWithProfile(1L, "owner", true);
        var viewer = TestFixtures.user(2L, "viewer");
        var follow = new Follow();
        follow.setStatus(FollowStatus.ACCEPTED);

        when(followRepository.findByFollowerAndFollowing(viewer, owner)).thenReturn(Optional.of(follow));
        when(followRepository.existsByFollowerAndFollowingAndStatus(viewer, owner, FollowStatus.ACCEPTED))
                .thenReturn(true);

        assertThat(profileAccessService.canViewFullProfile(owner, viewer)).isTrue();
        assertThat(profileAccessService.resolveFollowRelationStatus(owner, viewer))
                .isEqualTo(FollowRelationStatus.ACCEPTED);
    }

    @Test
    void should_throwAccessDenied_when_privateProfileIsNotAccessible() {
        var owner = TestFixtures.userWithProfile(1L, "owner", true);

        assertThatThrownBy(() -> profileAccessService.requireFullProfileAccess(owner))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_returnUser_when_usernameExistsAndProfileIsAccessible() {
        var owner = TestFixtures.userWithProfile(1L, "owner", false);

        when(userLookupService.requireByUsername("owner")).thenReturn(owner);

        var result = profileAccessService.requireUserWithFullProfileAccess("owner");

        assertThat(result).isEqualTo(owner);
    }
}
