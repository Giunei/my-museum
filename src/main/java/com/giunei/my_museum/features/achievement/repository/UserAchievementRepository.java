package com.giunei.my_museum.features.achievement.repository;

import com.giunei.my_museum.features.achievement.entity.UserAchievement;
import com.giunei.my_museum.features.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUserOrderByUnlockedAtDesc(User user);
    boolean existsByUserAndAchievement_Code(User user, String code);
}

