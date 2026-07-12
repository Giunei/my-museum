package com.giunei.my_museum.achievement.listener;

import com.giunei.my_museum.achievement.dto.AchievementResponse;
import com.giunei.my_museum.achievement.event.AchievementUnlockedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementEventListener {

	private final SimpMessagingTemplate messagingTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onAchievementUnlocked(AchievementUnlockedEvent event) {
		AchievementResponse payload = event.getAchievement();
		String username = event.getUser().getUsername();

		if (payload == null) {
			log.warn("Achievement unlocked without payload for user={} code={}",
					username, event.getAchievementCode());
			return;
		}

		messagingTemplate.convertAndSendToUser(username, "/queue/achievements", payload);
	}
}
