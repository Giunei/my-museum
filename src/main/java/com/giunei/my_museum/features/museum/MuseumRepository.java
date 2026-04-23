package com.giunei.my_museum.features.museum;

import com.giunei.my_museum.features.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MuseumRepository extends JpaRepository<Museum, Long> {
    Optional<Museum> findByUser(User user);
}
