package com.giunei.my_museum.media.repository;

import com.giunei.my_museum.media.entity.MediaCollection;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaCollectionRepository extends JpaRepository<MediaCollection, Long> {
    List<MediaCollection> findByUserAndType(User user, MediaType type);
    List<MediaCollection> findByUserAndTypeAndId(User user, MediaType type, Long id);
}
