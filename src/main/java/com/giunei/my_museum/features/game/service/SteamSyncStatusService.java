package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.features.game.dto.SteamSyncStatusResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SteamSyncStatusService {

    private static class SyncState {
        volatile boolean syncing;
        final AtomicInteger current = new AtomicInteger(0);
        volatile int total;
        volatile String message = "";
    }

    private final ConcurrentHashMap<Long, SyncState> syncStates = new ConcurrentHashMap<>();

    public void startSync(Long userId, int total) {
        SyncState state = syncStates.computeIfAbsent(userId, ignored -> new SyncState());
        state.syncing = true;
        state.current.set(0);
        state.total = total;
        state.message = "Iniciando sincronização...";
    }

    public void updateProgress(Long userId, int current, String message) {
        SyncState state = syncStates.get(userId);
        if (state != null) {
            state.current.set(current);
            state.message = message;
        }
    }

    public void completeSync(Long userId) {
        SyncState state = syncStates.get(userId);
        if (state != null) {
            state.syncing = false;
            state.current.set(state.total);
            state.message = "Sincronização concluída";
        }
    }

    public void failSync(Long userId, String error) {
        SyncState state = syncStates.computeIfAbsent(userId, ignored -> new SyncState());
        state.syncing = false;
        state.message = "Erro: " + error;
    }

    public SteamSyncStatusResponse getStatus(Long userId) {
        SyncState state = syncStates.get(userId);
        if (state == null) {
            return new SteamSyncStatusResponse(false, 0, 0, "Aguardando sincronização");
        }
        return new SteamSyncStatusResponse(
                state.syncing,
                state.current.get(),
                state.total,
                state.message
        );
    }

    public void clearState(Long userId) {
        syncStates.remove(userId);
    }
}
