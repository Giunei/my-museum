package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.dto.AuthResponse;
import com.giunei.my_museum.auth.dto.LoginRequest;
import com.giunei.my_museum.auth.entity.RefreshToken;
import com.giunei.my_museum.common.exception.InvalidPasswordOrUsernameException;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest extends AbstractUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthService authService;

    @Test
    void should_returnTokens_when_credentialsAreValid() {
        var user = TestFixtures.user(1L, "testuser");
        var refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(refreshTokenService.createToken(user)).thenReturn(refreshToken);
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.login(new LoginRequest("testuser", "password"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        verify(refreshTokenService).createToken(user);
    }

    @Test
    void should_throwInvalidCredentials_when_userDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("missing", "password")))
                .isInstanceOf(InvalidPasswordOrUsernameException.class)
                .hasMessage("Usuário ou senha inválida");
    }

    @Test
    void should_throwInvalidCredentials_when_passwordDoesNotMatch() {
        var user = TestFixtures.user(1L, "testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("testuser", "wrong")))
                .isInstanceOf(InvalidPasswordOrUsernameException.class)
                .hasMessage("Usuário ou senha inválida");
    }
}
