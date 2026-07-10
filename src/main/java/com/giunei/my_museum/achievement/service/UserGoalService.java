package com.giunei.my_museum.achievement.service;

import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.achievement.dto.UserGoalRequest;
import com.giunei.my_museum.achievement.dto.UserGoalResponse;
import com.giunei.my_museum.achievement.dto.UserGoalUpdateRequest;
import com.giunei.my_museum.achievement.entity.GoalType;
import com.giunei.my_museum.achievement.entity.UserGoal;
import com.giunei.my_museum.achievement.repository.UserGoalRepository;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
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
        validateGoalPeriod(request.startDate(), request.endDate());

        int progress = calculateProgressForGoal(user, request.type(), request.startDate(), request.endDate());

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

        validateGoalPeriod(request.startDate(), request.endDate());

        goal.setTarget(request.target());
        goal.setStartDate(request.startDate());
        goal.setEndDate(request.endDate());

        int progress = calculateProgressForGoal(user, goal.getType(), request.startDate(), request.endDate());

        goal.setProgress(progress);
        goal.setCompleted(progress >= goal.getTarget());

        repository.save(goal);

        return toResponse(goal);
    }

    @Transactional
    public List<UserGoalResponse> findMyGoals(MediaType type) {
        return findGoals(SecurityUtils.getAuthenticatedUser(), type);
    }

    @Transactional
    public List<UserGoalResponse> findGoals(User user, MediaType type) {
        if (type != null) {
            refreshGoalProgress(user, type);
        } else {
            Arrays.stream(MediaType.values()).forEach(mediaType -> refreshGoalProgress(user, mediaType));
        }

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
            throw new BusinessException("Não é possível excluir uma meta que já foi concluída");
        }

        repository.delete(goal);
    }

    @Transactional
    public void recalculateProgress(User user, MediaType type) {
        refreshGoalProgress(user, type);

        int totalCompleted = getTotalCompleted(user, type);

        switch (type) {
            case BOOK -> achievementService.awardReadCountAchievements(user, totalCompleted);
            case MOVIE -> achievementService.awardWatchCountAchievements(user, totalCompleted);
            case SERIES -> achievementService.awardSeriesWatchCountAchievements(user, totalCompleted);
            case GAME -> achievementService.awardGamePlayCountAchievements(user, totalCompleted);
        }

        achievementService.awardGoalCompletionAchievements(user);
    }

    @Transactional
    public void updateProgress(User user, MediaType type, LocalDate finishedAt) {
        if (finishedAt == null) {
            return;
        }
        refreshGoalProgress(user, type);
    }

    @Transactional
    public void refreshGoalProgress(User user, MediaType type) {
        List<UserGoal> goals = repository.findByUserAndType(user, type);

        for (UserGoal goal : goals) {
            int progress = calculateProgressForGoal(user, goal.getType(), goal.getStartDate(), goal.getEndDate());
            goal.setProgress(progress);
            goal.setCompleted(progress >= goal.getTarget());
        }

        repository.saveAll(goals);
    }

    private int calculateProgressForGoal(User user, MediaType type, LocalDate startDate, LocalDate endDate) {
        if (!hasValidGoalPeriod(startDate, endDate)) {
            return 0;
        }

        return (int) userMediaRepository.countCompletedInGoalPeriod(user, type, startDate, endDate);
    }

    private boolean hasValidGoalPeriod(LocalDate startDate, LocalDate endDate) {
        return startDate != null
                && endDate != null
                && !endDate.isBefore(startDate);
    }

    private void validateGoalPeriod(LocalDate startDate, LocalDate endDate) {
        if (!hasValidGoalPeriod(startDate, endDate)) {
            throw new BusinessException("A meta precisa de um período válido com data inicial e final");
        }
    }

    private int getTotalCompleted(User user, MediaType type) {
        return (int) userMediaRepository.countByUserAndTypeAndCompletedTrue(user, type);
    }

    private void validateUniqueGoal(User user, MediaType type, GoalType goalType) {
        boolean exists = repository.existsByUserAndTypeAndGoalType(user, type, goalType);

        if (exists) {
            throw new BusinessException("Você já possui uma meta desse tipo");
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
