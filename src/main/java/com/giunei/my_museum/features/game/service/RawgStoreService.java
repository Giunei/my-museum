package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.features.game.client.RawgClient;
import com.giunei.my_museum.features.game.dto.RawgGameResponse;
import com.giunei.my_museum.features.game.dto.RawgGameStoresResponse;
import com.giunei.my_museum.features.game.dto.StoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawgStoreService {

    private static final Map<Integer, String> KNOWN_STORE_NAMES = Map.of(
            1, "Steam",
            2, "Xbox Store",
            3, "PlayStation Store",
            4, "App Store",
            5, "GOG",
            6, "Nintendo Store",
            7, "Google Play",
            11, "Epic Games"
    );

    private final RawgClient rawgClient;

    public List<StoreInfo> applySteamFallback(List<StoreInfo> stores, String steamAppId) {
        return enrichWithSteamUrl(stores == null ? List.of() : stores, steamAppId);
    }

    public List<StoreInfo> resolveStoreLinksByRawgId(Long rawgId, String steamAppId) {
        if (rawgId == null) {
            return applySteamFallback(null, steamAppId);
        }
        return enrichWithSteamUrl(fetchStoreLinks(rawgId.intValue(), Map.of()), steamAppId);
    }

    public List<StoreInfo> resolveStoreInfos(RawgGameResponse.RawgGameItem item, String steamAppId) {
        if (item == null) {
            return enrichWithSteamUrl(List.of(), steamAppId);
        }

        Map<Integer, String> storeNames = buildStoreNameMap(item.stores());
        List<StoreInfo> stores = List.of();

        if (item.id() != null) {
            stores = fetchStoreLinks(item.id(), storeNames);
        }

        if (stores.isEmpty()) {
            stores = extractFromEmbeddedStores(item.stores());
        }

        return enrichWithSteamUrl(stores, steamAppId);
    }

    private List<StoreInfo> fetchStoreLinks(Integer rawgGameId, Map<Integer, String> storeNames) {
        try {
            RawgGameStoresResponse response = rawgClient.getGameStores(rawgGameId.longValue());
            if (response == null || response.results() == null || response.results().isEmpty()) {
                return List.of();
            }

            Map<String, StoreInfo> uniqueStores = new LinkedHashMap<>();
            for (RawgGameStoresResponse.GameStoreLink link : response.results()) {
                if (link.url() == null || link.url().isBlank()) {
                    continue;
                }

                Integer storeId = parseStoreId(link.storeId());
                String storeName = storeId != null
                        ? storeNames.getOrDefault(storeId, defaultStoreName(storeId))
                        : "Store";

                uniqueStores.putIfAbsent(storeName, new StoreInfo(storeName, link.url()));
            }

            return List.copyOf(uniqueStores.values());
        } catch (Exception e) {
            log.warn("Failed to fetch RAWG store links for game {}: {}", rawgGameId, e.getMessage());
            return List.of();
        }
    }

    private Map<Integer, String> buildStoreNameMap(List<RawgGameResponse.RawgStore> stores) {
        if (stores == null || stores.isEmpty()) {
            return Map.of();
        }

        Map<Integer, String> storeNames = new LinkedHashMap<>();
        for (RawgGameResponse.RawgStore store : stores) {
            if (store.store() == null || store.store().id() == null || store.store().name() == null) {
                continue;
            }
            storeNames.putIfAbsent(store.store().id(), store.store().name());
        }
        return storeNames;
    }

    private List<StoreInfo> extractFromEmbeddedStores(List<RawgGameResponse.RawgStore> stores) {
        if (stores == null || stores.isEmpty()) {
            return List.of();
        }

        Map<String, StoreInfo> uniqueStores = new LinkedHashMap<>();
        for (RawgGameResponse.RawgStore store : stores) {
            if (store.store() == null || store.store().name() == null) {
                continue;
            }

            String url = firstNonBlank(store.url());
            if (url == null) {
                continue;
            }

            uniqueStores.putIfAbsent(store.store().name(), new StoreInfo(store.store().name(), url));
        }

        return List.copyOf(uniqueStores.values());
    }

    private List<StoreInfo> enrichWithSteamUrl(List<StoreInfo> stores, String steamAppId) {
        if (steamAppId == null || steamAppId.isBlank()) {
            return stores.isEmpty() ? null : stores;
        }

        String steamUrl = "https://store.steampowered.com/app/" + steamAppId;
        List<StoreInfo> enriched = new ArrayList<>();
        boolean hasSteam = false;

        for (StoreInfo store : stores) {
            if ("Steam".equalsIgnoreCase(store.name())) {
                hasSteam = true;
                enriched.add(new StoreInfo(store.name(), firstNonBlank(store.url(), steamUrl)));
            } else {
                enriched.add(store);
            }
        }

        if (!hasSteam) {
            enriched.add(0, new StoreInfo("Steam", steamUrl));
        }

        return enriched.isEmpty() ? null : enriched;
    }

    private Integer parseStoreId(String storeId) {
        if (storeId == null || storeId.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(storeId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String defaultStoreName(Integer storeId) {
        return KNOWN_STORE_NAMES.getOrDefault(storeId, "Store");
    }

    @SafeVarargs
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
