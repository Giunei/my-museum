package com.giunei.my_museum.features.achievement.controller;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.achievement.dto.AchievementResponse;
import com.giunei.my_museum.features.achievement.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService service;

    @GetMapping
    public List<AchievementResponse> listMyAchievements() {
        var user = SecurityUtils.getAuthenticatedUser();

        return service.listForUser(user)
                .stream()
                .map(ua -> new AchievementResponse(
                        ua.getAchievement().getCode(),
                        ua.getAchievement().getName(),
                        ua.getAchievement().getDescription(),
                        ua.getAchievement().getImageUrl(),
                        ua.getUnlockedAt()
                ))
                .toList();
    }
}

