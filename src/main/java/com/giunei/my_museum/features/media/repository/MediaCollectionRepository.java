package com.giunei.my_museum.features.media.repository;

import com.giunei.my_museum.features.media.entity.MediaCollection;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaCollectionRepository extends JpaRepository<MediaCollection, Long> {
    List<MediaCollection> findByUserAndType(User user, MediaType type);
    List<MediaCollection> findByUserAndTypeAndId(User user, MediaType type, Long id);
}
