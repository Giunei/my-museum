package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamSyncService {

    private final SteamSyncProcessor syncProcessor;
    private final SteamSyncStatusService statusService;

    @Async("taskExecutor")
    public void sync(User user) {
        try {
            syncProcessor.runSync(user.getId());
        } catch (Exception e) {
            log.error("Error during Steam sync for user {}", user.getId(), e);
            String message = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
            statusService.failSync(user.getId(), message);
        }
    }
}
