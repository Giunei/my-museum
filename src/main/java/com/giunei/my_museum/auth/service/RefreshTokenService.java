package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.common.exception.ExpiredRefreshTokenException;
import com.giunei.my_museum.common.exception.InvalidRefreshTokenException;
import com.giunei.my_museum.auth.entity.RefreshToken;
import com.giunei.my_museum.auth.repository.RefreshTokenRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh.expiration-ms:1209600000}")
    private long refreshTokenExpirationMs;

    @Transactional
    public RefreshToken createToken(User user) {
        RefreshToken refreshToken = buildRefreshToken(user);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotate(String rawRefreshToken) {
        RefreshToken currentToken = findTokenByRawValue(rawRefreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido"));

        if (currentToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token inválido ou já utilizado");
        }

        if (currentToken.isExpired(LocalDateTime.now())) {
            currentToken.setRevokedAt(LocalDateTime.now());
            throw new ExpiredRefreshTokenException("Refresh token expirado");
        }

        currentToken.setRevokedAt(LocalDateTime.now());
        RefreshToken newToken = buildRefreshToken(currentToken.getUser());
        return refreshTokenRepository.save(newToken);
    }

    /**
     * Builds a new refresh token entity without persisting it.
     * This method is transaction-agnostic and can be called from within
     * other @Transactional methods without proxy issues.
     *
     * @param user the user to associate with the token
     * @return the built RefreshToken entity
     */
    private RefreshToken buildRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))
                .build();

        refreshToken.setToken(rawToken);
        return refreshToken;
    }

    /**
     * Finds a refresh token by comparing the raw token value against stored hashes.
     * Uses BCrypt password matching for secure comparison.
     *
     * @param rawRefreshToken the raw token value to search for
     * @return Optional containing the matching refresh token
     */
    private Optional<RefreshToken> findTokenByRawValue(String rawRefreshToken) {
        return refreshTokenRepository.findActiveTokens()
                .stream()
                .filter(token -> passwordEncoder.matches(rawRefreshToken, token.getTokenHash()))
                .findFirst();
    }
}



