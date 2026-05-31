package com.giunei.my_museum.features.achievement.event;

import com.giunei.my_museum.features.user.entity.User;
import org.springframework.context.ApplicationEvent;

public class AchievementUnlockedEvent extends ApplicationEvent {

    private final User user;
    private final String achievementCode;

    public AchievementUnlockedEvent(Object source, User user, String achievementCode) {
        super(source);
        this.user = user;
        this.achievementCode = achievementCode;
    }

    public User getUser() {
        return user;
    }

    public String getAchievementCode() {
        return achievementCode;
    }
}

