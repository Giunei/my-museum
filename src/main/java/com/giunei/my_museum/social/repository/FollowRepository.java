package com.giunei.my_museum.social.repository;

import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.entity.Follow;
import com.giunei.my_museum.social.entity.FollowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countByFollowingAndStatus(User user, FollowStatus status);

    long countByFollowerAndStatus(User user, FollowStatus status);

    boolean existsByFollowerAndFollowingAndStatus(User follower, User following, FollowStatus status);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    @EntityGraph(attributePaths = {"follower", "follower.profile"})
    Page<Follow> findByFollowing_IdAndStatusOrderByIdDesc(
            Long followingId,
            FollowStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"following", "following.profile"})
    Page<Follow> findByFollower_IdAndStatusOrderByIdDesc(
            Long followerId,
            FollowStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT f FROM Follow f
            JOIN FETCH f.follower follower
            LEFT JOIN FETCH follower.profile
            WHERE f.following.id = :ownerId AND f.status = :status
            """)
    List<Follow> findPendingIncomingRequests(
            @Param("ownerId") Long ownerId,
            @Param("status") FollowStatus status
    );

    @Query("""
            SELECT f.following, COUNT(f)
            FROM Follow f
            JOIN f.following.profile p
            WHERE f.status = com.giunei.my_museum.social.entity.FollowStatus.ACCEPTED
              AND p.privateProfile = false
            GROUP BY f.following
            ORDER BY COUNT(f) DESC
            """)
    List<Object[]> findMostFollowedUsers();
}
