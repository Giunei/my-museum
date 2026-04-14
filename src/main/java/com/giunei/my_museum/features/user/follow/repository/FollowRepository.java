package com.giunei.my_museum.features.user.follow.repository;

import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.follow.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countByFollowing(User user);

    long countByFollower(User user);

    boolean existsByFollowerAndFollowing(User follower, User following);
}
