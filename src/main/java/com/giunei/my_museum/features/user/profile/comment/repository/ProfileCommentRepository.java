package com.giunei.my_museum.features.user.profile.comment.repository;

import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.profile.comment.entity.ProfileComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ProfileCommentRepository extends JpaRepository<ProfileComment, Long> {

    Page<ProfileComment> findByProfileOwner(User profileOwner, Pageable pageable);

    boolean existsByAuthorAndProfileOwnerAndCreatedAtAfter(User author, User profileOwner, LocalDateTime dateTime);
}
