package com.giunei.my_museum.achievement.repository;

import com.giunei.my_museum.achievement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, String> {
}
