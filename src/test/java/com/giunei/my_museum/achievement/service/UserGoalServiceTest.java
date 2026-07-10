package com.giunei.my_museum.achievement.service;

import com.giunei.my_museum.achievement.dto.UserGoalRequest;
import com.giunei.my_museum.achievement.dto.UserGoalResponse;
import com.giunei.my_museum.achievement.entity.GoalType;
import com.giunei.my_museum.achievement.entity.UserGoal;
import com.giunei.my_museum.achievement.repository.UserGoalRepository;
import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserGoalServiceTest extends AbstractUnitTest {

    @Mock
    private UserGoalRepository repository;

    @Mock
    private UserMediaRepository userMediaRepository;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private UserGoalService userGoalService;

    @Test
    void should_returnGoals_when_userHasGoalsForType() {
        var user = TestFixtures.user(1L, "testuser");
        var goal = sampleGoal(user);

        when(repository.findByUserAndType(user, MediaType.BOOK)).thenReturn(List.of(goal));
        when(userMediaRepository.countCompletedInGoalPeriod(
                eq(user),
                eq(MediaType.BOOK),
                eq(goal.getStartDate()),
                eq(goal.getEndDate())
        )).thenReturn(2L);

        List<UserGoalResponse> responses = userGoalService.findGoals(user, MediaType.BOOK);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().target()).isEqualTo(5);
        assertThat(responses.getFirst().progress()).isEqualTo(2);
    }

    @Test
    void should_throwBusinessException_when_deletingCompletedGoal() {
        var user = TestFixtures.user(1L, "testuser");
        var completedGoal = sampleGoal(user);
        completedGoal.setCompleted(true);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);
            when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(completedGoal));

            assertThatThrownBy(() -> userGoalService.delete(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Não é possível excluir uma meta que já foi concluída");

            verify(repository, never()).delete(any());
        }
    }

    @Test
    void should_throwBusinessException_when_goalTypeAlreadyExists() {
        var user = TestFixtures.user(1L, "testuser");
        var request = new UserGoalRequest(
                MediaType.BOOK,
                GoalType.SHORT_TERM,
                5,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);
            when(repository.existsByUserAndTypeAndGoalType(user, MediaType.BOOK, GoalType.SHORT_TERM))
                    .thenReturn(true);

            assertThatThrownBy(() -> userGoalService.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Você já possui uma meta desse tipo");

            verify(repository, never()).save(any());
        }
    }

    @Test
    void should_createGoal_when_requestIsValid() {
        var user = TestFixtures.user(1L, "testuser");
        var request = new UserGoalRequest(
                MediaType.BOOK,
                GoalType.SHORT_TERM,
                5,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);
            when(repository.existsByUserAndTypeAndGoalType(user, MediaType.BOOK, GoalType.SHORT_TERM))
                    .thenReturn(false);
            when(userMediaRepository.countCompletedInGoalPeriod(
                    eq(user),
                    eq(MediaType.BOOK),
                    eq(request.startDate()),
                    eq(request.endDate())
            )).thenReturn(1L);
            when(repository.save(any(UserGoal.class))).thenAnswer(invocation -> {
                UserGoal goal = invocation.getArgument(0);
                goal.setId(10L);
                return goal;
            });

            UserGoalResponse response = userGoalService.create(request);

            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.target()).isEqualTo(5);
            assertThat(response.progress()).isEqualTo(1);
            assertThat(response.completed()).isFalse();
        }
    }

    private UserGoal sampleGoal(com.giunei.my_museum.user.entity.User user) {
        return UserGoal.builder()
                .id(1L)
                .user(user)
                .type(MediaType.BOOK)
                .goalType(GoalType.SHORT_TERM)
                .target(5)
                .progress(0)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .completed(false)
                .build();
    }
}
