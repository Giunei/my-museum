package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.entity.RefreshToken;
import com.giunei.my_museum.auth.repository.RefreshTokenRepository;
import com.giunei.my_museum.common.exception.ExpiredRefreshTokenException;
import com.giunei.my_museum.common.exception.InvalidRefreshTokenException;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceTest extends AbstractUnitTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 3_600_000L);
    }

    @Test
    void should_persistRefreshToken_when_userIsProvided() {
        var user = TestFixtures.user(1L, "testuser");

        when(passwordEncoder.encode(any())).thenReturn("hashed-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createToken(user);

        assertThat(token.getTokenHash()).isEqualTo("hashed-token");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getToken()).isNotBlank();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void should_rotateToken_when_refreshTokenIsValid() {
        var user = TestFixtures.user(1L, "testuser");
        var currentToken = RefreshToken.builder()
                .tokenHash("hashed-old")
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(refreshTokenRepository.findActiveTokens()).thenReturn(List.of(currentToken));
        when(passwordEncoder.matches("raw-token", "hashed-old")).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("hashed-new");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken rotated = refreshTokenService.rotate("raw-token");

        assertThat(currentToken.getRevokedAt()).isNotNull();
        assertThat(rotated.getTokenHash()).isEqualTo("hashed-new");
        assertThat(rotated.getUser()).isEqualTo(user);
    }

    @Test
    void should_throwInvalidRefreshToken_when_tokenDoesNotExist() {
        when(refreshTokenRepository.findActiveTokens()).thenReturn(List.of());

        assertThatThrownBy(() -> refreshTokenService.rotate("missing"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessage("Refresh token inválido");
    }

    @Test
    void should_throwExpiredRefreshToken_when_tokenIsExpired() {
        var user = TestFixtures.user(1L, "testuser");
        var expiredToken = RefreshToken.builder()
                .tokenHash("hashed-expired")
                .user(user)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(refreshTokenRepository.findActiveTokens()).thenReturn(List.of(expiredToken));
        when(passwordEncoder.matches("raw-token", "hashed-expired")).thenReturn(true);

        assertThatThrownBy(() -> refreshTokenService.rotate("raw-token"))
                .isInstanceOf(ExpiredRefreshTokenException.class)
                .hasMessage("Refresh token expirado");

        assertThat(expiredToken.getRevokedAt()).isNotNull();
    }
}
