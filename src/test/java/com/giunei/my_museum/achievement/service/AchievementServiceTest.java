package com.giunei.my_museum.achievement.service;

import com.giunei.my_museum.achievement.entity.Achievement;
import com.giunei.my_museum.achievement.entity.UserAchievement;
import com.giunei.my_museum.achievement.enums.AchievementType;
import com.giunei.my_museum.achievement.repository.AchievementRepository;
import com.giunei.my_museum.achievement.repository.UserAchievementRepository;
import com.giunei.my_museum.achievement.repository.UserGoalRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AchievementServiceTest extends AbstractUnitTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UserGoalRepository userGoalRepository;

    @Mock
    private ApplicationContext applicationContext;

    private AchievementService achievementService;

    @BeforeEach
    void setUp() {
        achievementService = new AchievementService(
                achievementRepository,
                userAchievementRepository,
                eventPublisher,
                userGoalRepository,
                applicationContext
        );
    }

    @Test
    void should_skipAward_when_achievementAlreadyExists() {
        var user = TestFixtures.user(1L, "testuser");

        when(userAchievementRepository.existsByUserAndAchievement_Code(user, "READ_FIRST_BOOK"))
                .thenReturn(true);

        achievementService.awardIfNotExists(user, "READ_FIRST_BOOK");

        verify(userAchievementRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void should_persistAchievement_when_achievementDoesNotExist() {
        var user = TestFixtures.user(1L, "testuser");
        var achievement = new Achievement();
        achievement.setCode("READ_FIRST_BOOK");

        when(userAchievementRepository.existsByUserAndAchievement_Code(user, "READ_FIRST_BOOK"))
                .thenReturn(false);
        when(achievementRepository.findById("READ_FIRST_BOOK")).thenReturn(Optional.of(achievement));
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        achievementService.awardIfNotExists(user, "READ_FIRST_BOOK");

        verify(userAchievementRepository).save(any(UserAchievement.class));
    }

    @Test
    void should_throwIllegalArgument_when_achievementCodeDoesNotExist() {
        var user = TestFixtures.user(1L, "testuser");

        when(userAchievementRepository.existsByUserAndAchievement_Code(user, "UNKNOWN"))
                .thenReturn(false);
        when(achievementRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> achievementService.awardIfNotExists(user, "UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Achievement not found: UNKNOWN");
    }

    @Test
    void should_awardReadAchievements_when_completedThresholdsAreReached() {
        var user = TestFixtures.user(1L, "testuser");

        when(applicationContext.getBean(AchievementService.class)).thenReturn(achievementService);
        when(userAchievementRepository.existsByUserAndAchievement_Code(eq(user), anyString()))
                .thenReturn(false);
        when(achievementRepository.findById(anyString())).thenAnswer(invocation -> {
            Achievement achievement = new Achievement();
            achievement.setCode(invocation.getArgument(0));
            return Optional.of(achievement);
        });
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<String> awarded = achievementService.awardReadCountAchievements(user, 10);

        assertThat(awarded).containsExactly("READ_FIRST_BOOK", "READ_5_BOOKS", "READ_10_BOOKS");
    }

    @Test
    void should_filterByType_when_listingUserAchievements() {
        var user = TestFixtures.user(1L, "testuser");

        achievementService.listForUser(user, AchievementType.BOOK);

        verify(userAchievementRepository)
                .findByUserAndAchievement_TypeOrderByUnlockedAtDesc(user, AchievementType.BOOK);
    }
}
