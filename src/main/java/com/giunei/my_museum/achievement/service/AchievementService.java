package com.giunei.my_museum.achievement.service;

import com.giunei.my_museum.achievement.dto.AchievementResponse;
import com.giunei.my_museum.achievement.entity.Achievement;
import com.giunei.my_museum.achievement.entity.UserAchievement;
import com.giunei.my_museum.achievement.enums.AchievementType;
import com.giunei.my_museum.achievement.event.AchievementUnlockedEvent;
import com.giunei.my_museum.achievement.repository.AchievementRepository;
import com.giunei.my_museum.achievement.repository.UserAchievementRepository;
import com.giunei.my_museum.achievement.repository.UserGoalRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

	private final AchievementRepository achievementRepository;
	private final UserAchievementRepository userAchievementRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final UserGoalRepository userGoalRepository;
	private final ApplicationContext applicationContext;

	public List<UserAchievement> listForUser(User user, AchievementType type) {
		if (type != null) {
			return userAchievementRepository.findByUserAndAchievement_TypeOrderByUnlockedAtDesc(user, type);
		}
		return userAchievementRepository.findByUserOrderByUnlockedAtDesc(user);
	}

	public long countForUser(User user) {
		return countForUser(user, null);
	}

	public long countForUser(User user, AchievementType type) {
		if (type != null) {
			return userAchievementRepository.countByUserAndAchievement_Type(user, type);
		}
		return userAchievementRepository.countByUser(user);
	}

	@Transactional
	public boolean awardIfNotExists(User user, String achievementCode) {
		if (userAchievementRepository.existsByUserAndAchievement_Code(user, achievementCode)) {
			return false;
		}

		Achievement achievement = achievementRepository.findById(achievementCode).orElse(null);
		if (achievement == null) {
			log.error("Skipping award: achievement not found code={}", achievementCode);
			return false;
		}

		LocalDateTime unlockedAt = LocalDateTime.now();
		UserAchievement ua = new UserAchievement();
		ua.setUser(user);
		ua.setAchievement(achievement);
		ua.setUnlockedAt(unlockedAt);

		userAchievementRepository.save(ua);

		AchievementResponse payload = new AchievementResponse(
				achievement.getCode(),
				achievement.getName(),
				achievement.getDescription(),
				achievement.getImageUrl(),
				unlockedAt
		);
		eventPublisher.publishEvent(new AchievementUnlockedEvent(this, user, achievementCode, payload));
		return true;
	}

	@Transactional
	public List<String> awardReadCountAchievements(User user, int totalCompleted) {
		return awardThresholdAchievements(
				user,
				totalCompleted,
				new int[]{1, 5, 10, 20},
				new String[]{"READ_FIRST_BOOK", "READ_5_BOOKS", "READ_10_BOOKS", "READ_20_BOOKS"}
		);
	}

	@Transactional
	public List<String> awardWatchCountAchievements(User user, int totalCompleted) {
		return awardThresholdAchievements(
				user,
				totalCompleted,
				new int[]{1, 5, 10},
				new String[]{"WATCH_FIRST_MOVIE", "WATCH_5_MOVIES", "WATCH_10_MOVIES"}
		);
	}

	@Transactional
	public List<String> awardSeriesWatchCountAchievements(User user, int totalCompleted) {
		return awardThresholdAchievements(
				user,
				totalCompleted,
				new int[]{1, 5, 10},
				new String[]{"WATCH_FIRST_SERIES", "WATCH_5_SERIES", "WATCH_10_SERIES"}
		);
	}

	@Transactional
	public List<String> awardGamePlayCountAchievements(User user, int totalCompleted) {
		return awardThresholdAchievements(
				user,
				totalCompleted,
				new int[]{1, 5, 10},
				new String[]{"COMPLETE_FIRST_GAME", "COMPLETE_5_GAMES", "COMPLETE_10_GAMES"}
		);
	}

	@Transactional
	public void awardGoalCompletionAchievements(User user) {
		long completedGoals = userGoalRepository.countByUserAndCompletedTrue(user);
		awardThresholdAchievements(
				user,
				(int) completedGoals,
				new int[]{1, 5, 10},
				new String[]{"COMPLETE_FIRST_GOAL", "COMPLETE_5_GOALS", "COMPLETE_10_GOALS"}
		);
	}

	private List<String> awardThresholdAchievements(User user, int count, int[] thresholds, String[] codes) {
		var awarded = new ArrayList<String>();
		var proxy = applicationContext.getBean(AchievementService.class);

		for (int i = 0; i < thresholds.length; i++) {
			if (count >= thresholds[i] && proxy.awardIfNotExists(user, codes[i])) {
				awarded.add(codes[i]);
			}
		}

		return awarded;
	}
}
