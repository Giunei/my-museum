package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.auth.service.JwtService;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamAuthService {

    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
    private static final String OPENID_NS = "http://specs.openid.net/auth/2.0";
    private static final String IDENTIFIER_SELECT = "http://specs.openid.net/auth/2.0/identifier_select";

    @Value("${steam.realm}")
    private String realm;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final SteamService steamService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public String generateAuthUrl() {
        User user = SecurityUtils.getAuthenticatedUser();
        String state = jwtService.generateSteamState(user.getId());

        String returnUrl = UriComponentsBuilder.fromUriString(realm)
                .path("/steam/callback")
                .queryParam("state", state)
                .build()
                .toUriString();

        return UriComponentsBuilder.fromUriString(STEAM_OPENID_URL)
                .queryParam("openid.ns", OPENID_NS)
                .queryParam("openid.mode", "checkid_setup")
                .queryParam("openid.claimed_id", IDENTIFIER_SELECT)
                .queryParam("openid.identity", IDENTIFIER_SELECT)
                .queryParam("openid.return_to", returnUrl)
                .queryParam("openid.realm", realm)
                .build()
                .toUriString();
    }

    @Transactional
    public String handleCallback(Map<String, String> params) {
        String mode = params.get("openid.mode");

        if ("cancel".equals(mode)) {
            throw new RuntimeException("Steam authentication cancelled by user");
        }

        if ("error".equals(mode)) {
            String error = params.get("openid.error");
            throw new RuntimeException("Steam authentication error: " + (error != null ? error : "Unknown error"));
        }

        if (!"id_res".equals(mode)) {
            throw new RuntimeException("Invalid OpenID mode: " + mode);
        }

        Map<String, String> openIdParams = params.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("openid."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!validateOpenIdResponse(openIdParams)) {
            throw new RuntimeException("Invalid OpenID response");
        }

        String state = params.get("state");
        if (state == null || !jwtService.isSteamStateValid(state)) {
            throw new RuntimeException("Invalid or expired Steam authentication state");
        }

        Long userId = jwtService.extractUserIdFromSteamState(state);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String identity = params.get("openid.claimed_id");
        if (identity == null) {
            identity = params.get("openid.identity");
        }

        String steamId64 = extractSteamId64(identity);
        steamService.connect(user, steamId64);

        return frontendUrl + "/steam/connected";
    }

    private boolean validateOpenIdResponse(Map<String, String> openIdParams) {
        try {
            String body = openIdParams.entrySet().stream()
                    .map(entry -> encodeFormField(
                            entry.getKey(),
                            "openid.mode".equals(entry.getKey()) ? "check_authentication" : entry.getValue()
                    ))
                    .collect(Collectors.joining("&"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(STEAM_OPENID_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.body() != null && response.body().contains("is_valid:true");
        } catch (Exception e) {
            log.error("Failed to validate Steam OpenID response", e);
            return false;
        }
    }

    private String encodeFormField(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8)
                + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String extractSteamId64(String identity) {
        if (identity == null) {
            throw new RuntimeException("Steam identity is null");
        }

        String[] parts = identity.split("/");
        return parts[parts.length - 1];
    }
}
