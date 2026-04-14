package com.giunei.my_museum.features.user.follow.service;

import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public int getFollowersCount(User user) {
        return (int) followRepository.countByFollowing(user);
    }

    public int getFollowingCount(User user) {
        return (int) followRepository.countByFollower(user);
    }
}
