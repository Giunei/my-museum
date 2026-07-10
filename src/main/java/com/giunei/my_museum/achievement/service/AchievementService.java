package com.giunei.my_museum.achievement.service;

import com.giunei.my_museum.achievement.entity.Achievement;
import com.giunei.my_museum.achievement.entity.UserAchievement;
import com.giunei.my_museum.achievement.enums.AchievementType;
import com.giunei.my_museum.achievement.event.AchievementUnlockedEvent;
import com.giunei.my_museum.achievement.repository.AchievementRepository;
import com.giunei.my_museum.achievement.repository.UserAchievementRepository;
import com.giunei.my_museum.achievement.repository.UserGoalRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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
	public void awardIfNotExists(User user, String achievementCode) {
		if (userAchievementRepository.existsByUserAndAchievement_Code(user, achievementCode)) {
			return;
		}

		Achievement achievement = achievementRepository.findById(achievementCode)
				.orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + achievementCode));

		UserAchievement ua = new UserAchievement();
		ua.setUser(user);
		ua.setAchievement(achievement);
		ua.setUnlockedAt(LocalDateTime.now());

		userAchievementRepository.save(ua);

		// publish an event so other layers (SSE/WebSocket) can notify the user in real-time
		eventPublisher.publishEvent(new AchievementUnlockedEvent(this, user, achievementCode));
	}

	@Transactional
	public List<String> awardReadCountAchievements(User user, int totalCompleted) {
		var awarded = new ArrayList<String>();

		int[] thresholds = {1, 5, 10, 20};
		String[] codes = {"READ_FIRST_BOOK", "READ_5_BOOKS", "READ_10_BOOKS", "READ_20_BOOKS"};

		var proxy = applicationContext.getBean(AchievementService.class);

		for (int i = 0; i < thresholds.length; i++) {
			int threshold = thresholds[i];
			String code = codes[i];

			if (totalCompleted >= threshold && !userAchievementRepository.existsByUserAndAchievement_Code(user, code)) {
				proxy.awardIfNotExists(user, code);
				awarded.add(code);
			}
		}

		return awarded;
	}

	@Transactional
	public List<String> awardRatingCountAchievements(User user, int ratedCount) {
		var awarded = new ArrayList<String>();
		var proxy = applicationContext.getBean(AchievementService.class);

		if (ratedCount >= 5 && !userAchievementRepository.existsByUserAndAchievement_Code(user, "RATE_5_BOOKS")) {
			proxy.awardIfNotExists(user, "RATE_5_BOOKS");
			awarded.add("RATE_5_BOOKS");
		}

		return awarded;
	}

	@Transactional
	public List<String> awardWatchCountAchievements(User user, int totalCompleted) {
		var awarded = new ArrayList<String>();

		int[] thresholds = {1, 5, 10};
		String[] codes = {"WATCH_FIRST_MOVIE", "WATCH_5_MOVIES", "WATCH_10_MOVIES"};

		var proxy = applicationContext.getBean(AchievementService.class);

		for (int i = 0; i < thresholds.length; i++) {
			int threshold = thresholds[i];
			String code = codes[i];

			if (totalCompleted >= threshold && !userAchievementRepository.existsByUserAndAchievement_Code(user, code)) {
				proxy.awardIfNotExists(user, code);
				awarded.add(code);
			}
		}

		return awarded;
	}

	@Transactional
	public List<String> awardSeriesWatchCountAchievements(User user, int totalCompleted) {
		var awarded = new ArrayList<String>();

		int[] thresholds = {1, 5, 10};
		String[] codes = {"WATCH_FIRST_SERIES", "WATCH_5_SERIES", "WATCH_10_SERIES"};

		var proxy = applicationContext.getBean(AchievementService.class);

		for (int i = 0; i < thresholds.length; i++) {
			int threshold = thresholds[i];
			String code = codes[i];

			if (totalCompleted >= threshold && !userAchievementRepository.existsByUserAndAchievement_Code(user, code)) {
				proxy.awardIfNotExists(user, code);
				awarded.add(code);
			}
		}

		return awarded;
	}

	@Transactional
	public List<String> awardGamePlayCountAchievements(User user, int totalCompleted) {
		var awarded = new ArrayList<String>();

		int[] thresholds = {1, 5, 10};
		String[] codes = {"COMPLETE_FIRST_GAME", "COMPLETE_5_GAMES", "COMPLETE_10_GAMES"};

		var proxy = applicationContext.getBean(AchievementService.class);

		for (int i = 0; i < thresholds.length; i++) {
			int threshold = thresholds[i];
			String code = codes[i];

			if (totalCompleted >= threshold && !userAchievementRepository.existsByUserAndAchievement_Code(user, code)) {
				proxy.awardIfNotExists(user, code);
				awarded.add(code);
			}
		}

		return awarded;
	}

	@Transactional
	public void awardGoalCompletionAchievements(User user) {
		long completedGoals = userGoalRepository.countByUserAndCompletedTrue(user);
		var proxy = applicationContext.getBean(AchievementService.class);

		if (completedGoals >= 1 && !userAchievementRepository.existsByUserAndAchievement_Code(user, "COMPLETE_FIRST_GOAL")) {
			proxy.awardIfNotExists(user, "COMPLETE_FIRST_GOAL");
		}

		if (completedGoals >= 5 && !userAchievementRepository.existsByUserAndAchievement_Code(user, "COMPLETE_5_GOALS")) {
			proxy.awardIfNotExists(user, "COMPLETE_5_GOALS");
		}
	}
}
