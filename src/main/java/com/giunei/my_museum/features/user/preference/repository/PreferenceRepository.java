package com.giunei.my_museum.features.user.preference.repository;

import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    List<Preference> findByUser(User user);

    void deleteByUser(User user);
}
