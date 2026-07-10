package com.giunei.my_museum.game.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserGameAchievement extends EntityAbstract {

    @ManyToOne
    private UserGame userGame;

    private String achievementCode;

    private String name;

    private String description;

    private String iconUrl;

    private boolean unlocked;

    private LocalDateTime unlockedAt;
}
