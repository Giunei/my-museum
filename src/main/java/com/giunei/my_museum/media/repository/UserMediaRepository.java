package com.giunei.my_museum.media.repository;

import com.giunei.my_museum.media.entity.MediaCollection;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserMediaRepository extends JpaRepository<UserMedia, Long> {

    Page<UserMedia> findByUser(User user, Pageable pageable);

    Page<UserMedia> findByUserAndType(User user, MediaType type, Pageable pageable);

    Page<UserMedia> findByUserAndTypeAndCompleted(User user, MediaType type, boolean completed, Pageable pageable);

    @Query("SELECT um FROM UserMedia um JOIN um.collections c WHERE um.user = :user AND c = :collection")
    Page<UserMedia> findByUserAndCollection(User user, MediaCollection collection, Pageable pageable);

    long countByUserAndTypeAndCompletedTrue(User user, MediaType type);

    long countByUserAndTypeAndRatingIsNotNull(User user, MediaType type);

    long countByUserAndTypeAndHighlightedTrue(User user, MediaType type);

    @Query("""
            SELECT COUNT(um) FROM UserMedia um
            WHERE um.user = :user
            AND um.type = :type
            AND um.completed = true
            AND um.finishedAt IS NOT NULL
            AND um.finishedAt BETWEEN :start AND :end
            """)
    long countCompletedInGoalPeriod(User user, MediaType type, java.time.LocalDate start, java.time.LocalDate end);

    @Modifying
    @Query("update UserMedia u set u.highlighted = false where u.user = :user")
    void clearHighlights(User user);

    List<UserMedia> findByUserAndTypeAndHighlightedTrueOrderByDisplayOrderAsc(
            User user,
            MediaType type,
            Pageable pageable
    );

    Optional<UserMedia> findByIdAndUser(Long id, User user);

    long countByUserAndHighlightedTrue(User user);

    @Query("select coalesce(max(u.displayOrder), -1) + 1 from UserMedia u where u.user = :user")
    Integer getNextDisplayOrder(User user);

    boolean existsByExternalIdAndUser(String externalId, User user);

    Optional<UserMedia> findByUserAndExternalId(User user, String externalId);

    List<UserMedia> findByUserAndFinishedAtIsNotNullOrderByFinishedAtDesc(User user);

    List<UserMedia> findByUserAndTypeAndFinishedAtIsNotNullOrderByFinishedAtDesc(User user, MediaType type);

    Page<UserMedia> findByUserAndTypeAndStatus(User user, MediaType type, MediaStatus status, Pageable pageable);

    long countByUser(User user);

    long countByRatingIsNotNull();

    long countByUserAndRatingIsNotNull(User user);
}