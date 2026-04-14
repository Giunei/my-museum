package com.giunei.my_museum.features.user.profile.repository;

import com.giunei.my_museum.features.user.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
