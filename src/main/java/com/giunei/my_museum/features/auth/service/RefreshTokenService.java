package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.exceptions.ExpiredRefreshTokenException;
import com.giunei.my_museum.exceptions.InvalidRefreshTokenException;
import com.giunei.my_museum.features.auth.entity.RefreshToken;
import com.giunei.my_museum.features.auth.repository.RefreshTokenRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration-ms:1209600000}")
    private long refreshTokenExpirationMs;

    @Transactional
    public RefreshToken createToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotate(String rawRefreshToken) {
        RefreshToken currentToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido"));

        if (currentToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token inválido ou já utilizado");
        }

        if (currentToken.isExpired(LocalDateTime.now())) {
            currentToken.setRevokedAt(LocalDateTime.now());
            throw new ExpiredRefreshTokenException("Refresh token expirado");
        }

        currentToken.setRevokedAt(LocalDateTime.now());
        return createToken(currentToken.getUser());
    }
}

