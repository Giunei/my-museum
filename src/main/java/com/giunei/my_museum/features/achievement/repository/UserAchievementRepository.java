package com.giunei.my_museum.features.achievement.repository;

import com.giunei.my_museum.features.achievement.entity.UserAchievement;
import com.giunei.my_museum.features.achievement.enums.AchievementType;
import com.giunei.my_museum.features.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUserOrderByUnlockedAtDesc(User user);
    boolean existsByUserAndAchievement_Code(User user, String code);
    long countByUser(User user);

    @SuppressWarnings("unused")
    Optional<UserAchievement> findByUserAndAchievement_Code(User user, String code);

    @Query("SELECT ua FROM UserAchievement ua " +
            "JOIN ua.achievement a " +
            "WHERE ua.user = :user " +
                "AND a.type = :type " +
            "ORDER BY ua.unlockedAt DESC")
    List<UserAchievement> findByUserAndAchievement_TypeOrderByUnlockedAtDesc(User user, AchievementType type);
}

