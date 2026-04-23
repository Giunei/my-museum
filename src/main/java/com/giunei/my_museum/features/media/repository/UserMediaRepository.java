package com.giunei.my_museum.features.media.repository;

import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.user.entity.User;
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

    @Modifying
    @Query("update UserMedia u set u.highlighted = false where u.user = :user")
    void clearHighlights(User user);

    List<UserMedia> findTop6ByUserAndTypeAndHighlightedTrueOrderByDisplayOrderAsc(
            User user,
            MediaType type
    );

    Optional<UserMedia> findByIdAndUser(Long id, User user);

    long countByUserAndHighlightedTrue(User user);

    @Query("select coalesce(max(u.displayOrder), -1) + 1 from UserMedia u where u.user = :user")
    Integer getNextDisplayOrder(User user);
}