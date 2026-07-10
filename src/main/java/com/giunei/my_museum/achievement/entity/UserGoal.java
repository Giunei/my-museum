package com.giunei.my_museum.achievement.entity;

import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    private Integer target;

    private Integer progress;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean completed;
}

