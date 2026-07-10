package com.giunei.my_museum.achievement.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.achievement.dto.AchievementResponse;
import com.giunei.my_museum.achievement.enums.AchievementType;
import com.giunei.my_museum.achievement.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService service;

    @GetMapping
    public List<AchievementResponse> listMyAchievements(
            @RequestParam(required = false) AchievementType type
    ) {
        var user = SecurityUtils.getAuthenticatedUser();

        return service.listForUser(user, type)
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

    @GetMapping("/count")
    public long countMyAchievements(
            @RequestParam(required = false) AchievementType type
    ) {
        var user = SecurityUtils.getAuthenticatedUser();
        return service.countForUser(user, type);
    }
}

