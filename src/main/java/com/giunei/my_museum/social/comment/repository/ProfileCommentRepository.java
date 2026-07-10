package com.giunei.my_museum.social.comment.repository;

import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.comment.entity.ProfileComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProfileCommentRepository extends JpaRepository<ProfileComment, Long> {

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<ProfileComment> findByProfileOwner(User profileOwner, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    @Query("SELECT c FROM ProfileComment c WHERE c.id = :id")
    Optional<ProfileComment> findWithAuthorById(@Param("id") Long id);

    boolean existsByAuthorAndProfileOwnerAndCreatedAtAfter(User author, User profileOwner, LocalDateTime dateTime);
}
