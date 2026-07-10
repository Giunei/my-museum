package com.giunei.my_museum.social.service;

import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.common.exception.UserNotFoundException;
import com.giunei.my_museum.profile.FollowRelationStatus;
import com.giunei.my_museum.profile.service.ProfileAccessService;
import com.giunei.my_museum.social.entity.Follow;
import com.giunei.my_museum.social.entity.FollowStatus;
import com.giunei.my_museum.social.repository.FollowRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FollowServiceTest extends AbstractUnitTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileAccessService profileAccessService;

    @InjectMocks
    private FollowService followService;

    @Test
    void should_createAcceptedFollow_when_profileIsPublic() {
        var follower = TestFixtures.user(1L, "follower");
        var following = TestFixtures.userWithProfile(2L, "following", false);

        when(userRepository.findByUsername("following")).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());
        when(profileAccessService.isPrivateProfile(following)).thenReturn(false);

        FollowRelationStatus status = followService.follow(follower, "following");

        assertThat(status).isEqualTo(FollowRelationStatus.ACCEPTED);

        ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(FollowStatus.ACCEPTED);
    }

    @Test
    void should_createPendingFollow_when_profileIsPrivate() {
        var follower = TestFixtures.user(1L, "follower");
        var following = TestFixtures.userWithProfile(2L, "following", true);

        when(userRepository.findByUsername("following")).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());
        when(profileAccessService.isPrivateProfile(following)).thenReturn(true);

        FollowRelationStatus status = followService.follow(follower, "following");

        assertThat(status).isEqualTo(FollowRelationStatus.PENDING);
    }

    @Test
    void should_throwBusinessException_when_userFollowsHimself() {
        var user = TestFixtures.user(1L, "sameuser");

        when(userRepository.findByUsername("sameuser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> followService.follow(user, "sameuser"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Você não pode seguir a si mesmo");

        verify(followRepository, never()).save(any());
    }

    @Test
    void should_throwUserNotFound_when_targetDoesNotExist() {
        var follower = TestFixtures.user(1L, "follower");
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.follow(follower, "missing"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void should_deleteFollow_when_relationshipExists() {
        var follower = TestFixtures.user(1L, "follower");
        var following = TestFixtures.user(2L, "following");
        var follow = new Follow();

        when(userRepository.findByUsername("following")).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(follow));

        followService.unfollow(follower, "following");

        verify(followRepository).delete(follow);
    }
}
