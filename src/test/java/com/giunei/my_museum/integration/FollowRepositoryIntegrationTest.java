package com.giunei.my_museum.integration;

import com.giunei.my_museum.auth.service.UserProfileService;
import com.giunei.my_museum.social.entity.Follow;
import com.giunei.my_museum.social.entity.FollowStatus;
import com.giunei.my_museum.social.repository.FollowRepository;
import com.giunei.my_museum.support.AbstractIntegrationTest;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class FollowRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileService userProfileService;

    private User follower;
    private User following;

    @BeforeEach
    void setUpUsers() {
        follower = persistUser("follower");
        following = persistUser("following");
    }

    @Test
    void should_persistAndFindFollow_when_relationshipIsCreated() {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(follow);

        var found = followRepository.findByFollowerAndFollowing(follower, following);

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(FollowStatus.ACCEPTED);
        assertThat(followRepository.countByFollowingAndStatus(following, FollowStatus.ACCEPTED)).isEqualTo(1);
    }

    @Test
    void should_returnPendingRequests_when_followStatusIsPending() {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(FollowStatus.PENDING);
        followRepository.save(follow);

        var requests = followRepository.findPendingIncomingRequests(following.getId(), FollowStatus.PENDING);

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getFollower().getUsername()).isEqualTo("follower");
    }

    private User persistUser(String username) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .email(username + "@test.com")
                .emailVerified(true)
                .build();
        userRepository.save(user);
        userProfileService.createProfileForUser(user);
        return user;
    }
}
