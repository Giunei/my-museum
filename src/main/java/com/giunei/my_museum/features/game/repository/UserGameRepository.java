package com.giunei.my_museum.features.game.repository;

import com.giunei.my_museum.features.game.entity.UserGame;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    Optional<UserGame> findByMediaId(Long mediaId);

    @Query("""
            SELECT ug FROM UserGame ug
            JOIN FETCH ug.media m
            WHERE ug.steamAppId = :steamAppId AND m.user.id = :userId
            """)
    Optional<UserGame> findBySteamAppIdAndUserId(
            @Param("steamAppId") String steamAppId,
            @Param("userId") Long userId
    );

    @Query("""
            SELECT DISTINCT ug FROM UserGame ug
            JOIN FETCH ug.media m
            WHERE m.user.id = :userId
            """)
    List<UserGame> findAllWithMediaByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT ug FROM UserGame ug
            JOIN FETCH ug.media m
            WHERE m.user.id = :userId AND ug.status = :status
            """)
    List<UserGame> findAllWithMediaByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") MediaStatus status
    );

    @Query("""
            SELECT DISTINCT ug FROM UserGame ug
            JOIN FETCH ug.media m
            WHERE m.user.id = :userId AND m.highlighted = true
            ORDER BY m.displayOrder ASC
            """)
    List<UserGame> findHighlightedWithMediaByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT ug FROM UserGame ug
            JOIN FETCH ug.media m
            WHERE m.user.id = :userId
            ORDER BY ug.playtimeMinutes DESC
            """)
    List<UserGame> findAllWithMediaByUserIdOrderByPlaytimeDesc(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(ug) FROM UserGame ug
            WHERE ug.media.user.id = :userId
            """)
    long countByUserId(@Param("userId") Long userId);
}
