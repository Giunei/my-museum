package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.features.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String STEAM_STATE_TYPE = "steam_state";
    private static final String USER_ID_CLAIM = "user_id";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration-ms:3600000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.steam-state.expiration-ms:300000}")
    private long steamStateExpirationMs;

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateSteamState(Long userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + steamStateExpirationMs);

        return Jwts.builder()
                .claim(TOKEN_TYPE_CLAIM, STEAM_STATE_TYPE)
                .claim(USER_ID_CLAIM, userId)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Long extractUserIdFromSteamState(String state) {
        Claims claims = parseClaims(state);
        Object userId = claims.get(USER_ID_CLAIM);
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        throw new IllegalArgumentException("Invalid user ID in state");
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername())
                && !isTokenExpired(token)
                && isAccessToken(token);
    }

    public boolean isSteamStateValid(String state) {
        try {
            Claims claims = parseClaims(state);
            return STEAM_STATE_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM))
                    && !isTokenExpired(state);
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private boolean isAccessToken(String token) {
        Object tokenType = parseClaims(token).get(TOKEN_TYPE_CLAIM);
        return ACCESS_TOKEN_TYPE.equals(tokenType);
    }

    private Claims parseClaims(String token) {
        return getParser()
                .parseSignedClaims(token)
                .getPayload();
    }

    private JwtParser getParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
    }
}
