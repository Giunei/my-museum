package com.giunei.my_museum.features.achievement.listener;

import com.giunei.my_museum.features.achievement.dto.AchievementResponse;
import com.giunei.my_museum.features.achievement.event.AchievementUnlockedEvent;
import com.giunei.my_museum.features.achievement.repository.UserAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AchievementEventListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final UserAchievementRepository userAchievementRepository;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onAchievementUnlocked(AchievementUnlockedEvent event) {
		userAchievementRepository.findByUserAndAchievement_Code(event.getUser(), event.getAchievementCode())
				.ifPresent(userAchievement -> messagingTemplate.convertAndSendToUser(
						event.getUser().getUsername(),
						"/queue/achievements",
						new AchievementResponse(
							userAchievement.getAchievement().getCode(),
							userAchievement.getAchievement().getName(),
							userAchievement.getAchievement().getDescription(),
							userAchievement.getAchievement().getImageUrl(),
							userAchievement.getUnlockedAt()
						)
				));
	}
}




