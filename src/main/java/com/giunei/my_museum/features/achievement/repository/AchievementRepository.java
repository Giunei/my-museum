package com.giunei.my_museum.features.achievement.repository;

import com.giunei.my_museum.features.achievement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, String> {
}
