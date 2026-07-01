package com.giunei.my_museum.features.lol.controller;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.lol.dto.ConnectLolRequest;
import com.giunei.my_museum.features.lol.dto.LolConnectionStatusResponse;
import com.giunei.my_museum.features.lol.dto.LolRankResponse;
import com.giunei.my_museum.features.lol.service.LolService;
import com.giunei.my_museum.features.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lol")
@RequiredArgsConstructor
public class LolController {

    private final LolService lolService;

    @PostMapping("/connect")
    public LolRankResponse connect(@Valid @RequestBody ConnectLolRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        return lolService.connect(user, request);
    }

    @GetMapping("/connection-status")
    public LolConnectionStatusResponse connectionStatus() {
        User user = SecurityUtils.getAuthenticatedUser();
        return lolService.getConnectionStatus(user);
    }

    @GetMapping("/rank")
    public LolRankResponse rank() {
        User user = SecurityUtils.getAuthenticatedUser();
        return lolService.getRank(user);
    }

    @PostMapping("/rank/refresh")
    public LolRankResponse refreshRank() {
        User user = SecurityUtils.getAuthenticatedUser();
        return lolService.refreshRank(user);
    }
}
