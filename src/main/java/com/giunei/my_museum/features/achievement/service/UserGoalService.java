package com.giunei.my_museum.features.achievement.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.achievement.dto.UserGoalRequest;
import com.giunei.my_museum.features.achievement.dto.UserGoalResponse;
import com.giunei.my_museum.features.achievement.dto.UserGoalUpdateRequest;
import com.giunei.my_museum.features.achievement.entity.GoalType;
import com.giunei.my_museum.features.achievement.entity.UserGoal;
import com.giunei.my_museum.features.achievement.repository.UserGoalRepository;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGoalService {

    private final UserGoalRepository repository;
    private final UserMediaRepository userMediaRepository;
    private final AchievementService achievementService;

    public UserGoalResponse create(UserGoalRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        validateUniqueGoal(user, request.type(), request.goalType());

        int progress = getCurrentProgress(user, request.type());

        UserGoal goal = UserGoal.builder()
                .user(user)
                .type(request.type())
                .goalType(request.goalType())
                .target(request.target())
                .progress(progress)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .completed(progress >= request.target())
                .build();

        repository.save(goal);

        return toResponse(goal);
    }

    public UserGoalResponse update(Long id, UserGoalUpdateRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        UserGoal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));

        goal.setTarget(request.target());
        goal.setStartDate(request.startDate());
        goal.setEndDate(request.endDate());

        int progress = getCurrentProgress(user, goal.getType());

        goal.setProgress(progress);
        goal.setCompleted(progress >= goal.getTarget());

        repository.save(goal);

        return toResponse(goal);
    }

    public List<UserGoalResponse> findMyGoals(MediaType type) {
        User user = SecurityUtils.getAuthenticatedUser();

        List<UserGoal> goals = (type != null)
                ? repository.findByUserAndType(user, type)
                : repository.findByUser(user);

        return goals.stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        User user = SecurityUtils.getAuthenticatedUser();

        UserGoal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));

        if (goal.isCompleted()) {
            throw new RuntimeException("Não é possível excluir uma meta que já foi concluída");
        }

        repository.delete(goal);
    }

    @Transactional
    public void recalculateProgress(User user, MediaType type) {

        // total completed (all time) used for read-count achievements
        int totalCompleted = getCurrentProgress(user, type);

        List<UserGoal> goals = repository.findByUserAndType(user, type);

        for (UserGoal goal : goals) {
            // Only count medias finished within the goal's date range.
            // If the goal has no start/end date defined, we do not consider any medias for it.
            if (goal.getStartDate() != null && goal.getEndDate() != null
                    && !goal.getEndDate().isBefore(goal.getStartDate())) {
                long countBetween = userMediaRepository
                        .countByUserAndTypeAndCompletedTrueAndFinishedAtBetween(
                                user,
                                goal.getType(),
                                goal.getStartDate(),
                                goal.getEndDate()
                        );
                goal.setProgress((int) countBetween);
                goal.setCompleted(goal.getProgress() >= goal.getTarget());
            } else {
                // If goal has no valid date range, progress is considered zero for date-bound counting
                goal.setProgress(0);
                goal.setCompleted(false);
            }
        }

        // Persist changes (entities are managed in the transaction, but saveAll makes intent explicit)
        repository.saveAll(goals);

        // Award read-count achievements based on total completed items for this media type
        achievementService.awardReadCountAchievements(user, totalCompleted);

        // Award achievements based on goal completions
        achievementService.awardGoalCompletionAchievements(user);
    }

    @Transactional
    public void updateProgress(User user, MediaType type) {
        // Deprecated behavior: increments progress for active goals by 1.
        // Prefer calling recalculateProgress(user, type) to compute progress based on finishedAt dates.
        List<UserGoal> goals = repository.findActiveGoals(user, type, LocalDate.now());

        for (UserGoal goal : goals) {
            goal.setProgress(goal.getProgress() + 1);

            if (goal.getProgress() >= goal.getTarget()) {
                goal.setCompleted(true);
            }
        }
    }

    /**
     * Increment progress for active goals only if the provided finishedAt falls within the goal's date range.
     * This is useful when handling a single media marked as finished.
     */
    @Transactional
    public void updateProgress(User user, MediaType type, LocalDate finishedAt) {
        if (finishedAt == null) {
            return;
        }

        List<UserGoal> goals = repository.findActiveGoals(user, type, finishedAt);

        for (UserGoal goal : goals) {
            // Only increment if finishedAt is within start/end (findActiveGoals already ensures this)
            goal.setProgress(goal.getProgress() + 1);

            if (goal.getProgress() >= goal.getTarget()) {
                goal.setCompleted(true);
            }
        }
    }

    private int getCurrentProgress(User user, MediaType type) {
        return (int) userMediaRepository
                .countByUserAndTypeAndCompletedTrue(user, type);
    }

    private void validateUniqueGoal(User user, MediaType type, GoalType goalType) {
        boolean exists = repository.existsByUserAndTypeAndGoalType(user, type, goalType);

        if (exists) {
            throw new RuntimeException("Você já possui uma meta desse tipo");
        }
    }


    private UserGoalResponse toResponse(UserGoal goal) {
        return new UserGoalResponse(
                goal.getId(),
                goal.getType(),
                goal.getGoalType(),
                goal.getTarget(),
                goal.getProgress(),
                goal.getStartDate(),
                goal.getEndDate(),
                goal.isCompleted()
        );
    }
}
