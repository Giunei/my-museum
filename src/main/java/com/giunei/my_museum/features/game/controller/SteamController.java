package com.giunei.my_museum.features.game.controller;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.game.dto.ConnectSteamRequest;
import com.giunei.my_museum.features.game.dto.SteamConnectionStatusResponse;
import com.giunei.my_museum.features.game.dto.SteamSyncStatusResponse;
import com.giunei.my_museum.features.game.dto.SteamSummaryResponse;
import com.giunei.my_museum.features.game.service.SteamAuthService;
import com.giunei.my_museum.features.game.service.SteamService;
import com.giunei.my_museum.features.game.service.SteamSyncService;
import com.giunei.my_museum.features.game.service.SteamSyncStatusService;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/steam")
@RequiredArgsConstructor
public class SteamController {

    private final SteamService steamService;
    private final SteamSyncService syncService;
    private final SteamAuthService steamAuthService;
    private final SteamSyncStatusService statusService;

    @GetMapping("/auth-url")
    public String getAuthUrl() {
        return steamAuthService.generateAuthUrl();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> params) {
        String redirectUrl = steamAuthService.handleCallback(params);
        return ResponseEntity.status(302).header("Location", redirectUrl).build();
    }

    @PostMapping("/connect")
    public void connect(@RequestBody ConnectSteamRequest request) {
        steamService.connect(request.steamId64());
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> sync() {
        User user = SecurityUtils.getAuthenticatedUser();
        
        // Check if already syncing
        SteamSyncStatusResponse currentStatus = statusService.getStatus(user.getId());
        if (currentStatus.syncing()) {
            return ResponseEntity.accepted().build();
        }
        
        // Mark as syncing before starting async sync
        statusService.startSync(user.getId(), 0);
        syncService.sync(user);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/sync/status")
    public SteamSyncStatusResponse syncStatus() {
        User user = SecurityUtils.getAuthenticatedUser();
        return statusService.getStatus(user.getId());
    }

    @GetMapping("/summary")
    public SteamSummaryResponse summary() {
        return steamService.getSummary();
    }

    @GetMapping("/connection-status")
    public SteamConnectionStatusResponse connectionStatus() {
        return steamService.getConnectionStatus();
    }
}
