package com.giunei.my_museum.achievement.repository;

import com.giunei.my_museum.achievement.entity.GoalType;
import com.giunei.my_museum.achievement.entity.UserGoal;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    List<UserGoal> findByUser(User user);

    List<UserGoal> findByUserAndType(User user, MediaType type);

    Optional<UserGoal> findByIdAndUser(Long id, User user);

    boolean existsByUserAndTypeAndGoalType(User user, MediaType type, GoalType goalType);

    @Query("""
        SELECT g FROM UserGoal g
        WHERE g.user = :user
        AND g.type = :type
        AND g.completed = false
        AND :today BETWEEN g.startDate AND g.endDate
    """)
    List<UserGoal> findActiveGoals(User user, MediaType type, LocalDate today);

    long countByUserAndCompletedTrue(User user);
}
