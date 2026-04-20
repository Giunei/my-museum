package com.giunei.my_museum.features.media.repository;

import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMediaRepository extends JpaRepository<UserMedia, Long> {

    Page<UserMedia> findByUser(User user, Pageable pageable);

    Page<UserMedia> findByUserAndType(User user, MediaType type, Pageable pageable);

    Page<UserMedia> findByUserAndTypeAndCompletedTrue(User user, MediaType type, Pageable pageable);
}