package com.giunei.my_museum.achievement.event;

import com.giunei.my_museum.user.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AchievementUnlockedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final transient User user;
    private final String achievementCode;

    public AchievementUnlockedEvent(Object source, User user, String achievementCode) {
        super(source);
        this.user = user;
        this.achievementCode = achievementCode;
    }
}

