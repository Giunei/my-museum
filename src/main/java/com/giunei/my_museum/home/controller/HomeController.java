package com.giunei.my_museum.home.controller;

import com.giunei.my_museum.home.dto.HomeStatisticsResponse;
import com.giunei.my_museum.home.dto.PopularProfileResponse;
import com.giunei.my_museum.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/statistics")
    public HomeStatisticsResponse getStatistics() {
        return homeService.getStatistics();
    }

    @GetMapping("/popular-profiles")
    public List<PopularProfileResponse> getPopularProfiles() {
        return homeService.getPopularProfiles();
    }
}
