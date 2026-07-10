package com.giunei.my_museum.media.controller;

import com.giunei.my_museum.media.dto.RecentActivityResponse;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.RecentActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class RecentActivityController {

    private final RecentActivityService recentActivityService;

    @GetMapping("/recent")
    public List<RecentActivityResponse> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) MediaType type
    ) {
        return recentActivityService.getRecentActivities(limit, type);
    }
}
