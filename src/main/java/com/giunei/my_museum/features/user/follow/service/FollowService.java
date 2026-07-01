package com.giunei.my_museum.features.user.follow.service;

import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.follow.Follow;
import com.giunei.my_museum.features.user.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public int getFollowersCount(User user) {
        return (int) followRepository.countByFollowing(user);
    }

    public int getFollowingCount(User user) {
        return (int) followRepository.countByFollower(user);
    }

    @Transactional
    public void follow(User follower, String followingUsername) {
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("You already follow this user");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(User follower, String followingUsername) {
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalArgumentException("You are not following this user"));

        followRepository.delete(follow);
    }
}
