package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.entity.PasswordResetToken;
import com.giunei.my_museum.auth.repository.PasswordResetTokenRepository;
import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordResetServiceTest extends AbstractUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void should_returnGenericMessage_when_emailIsNotRegistered() {
        when(userRepository.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        String message = passwordResetService.requestPasswordReset("missing@test.com");

        assertThat(message).contains("Se o email estiver cadastrado");
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetCodeEmail(any(), any());
    }

    @Test
    void should_createResetToken_when_emailExists() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmail("testuser@test.com");

        when(userRepository.findByEmailIgnoreCase("testuser@test.com")).thenReturn(Optional.of(user));

        String message = passwordResetService.requestPasswordReset("testuser@test.com");

        assertThat(message).contains("Se o email estiver cadastrado");
        verify(tokenRepository).deleteByUser(user);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetCodeEmail(eq(user.getEmail()), anyString());
    }

    @Test
    void should_resetPassword_when_codeIsValid() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmail("testuser@test.com");
        var resetToken = PasswordResetToken.builder()
                .token("123456")
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(userRepository.findByEmailIgnoreCase("testuser@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        String message = passwordResetService.resetPassword(
                "testuser@test.com",
                "123456",
                "new-password"
        );

        assertThat(message).isEqualTo("Senha redefinida com sucesso!");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        verify(tokenRepository).deleteByUser(user);
    }

    @Test
    void should_throwBusinessException_when_resetCodeIsInvalid() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmail("testuser@test.com");

        when(userRepository.findByEmailIgnoreCase("testuser@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken("000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword(
                "testuser@test.com",
                "000000",
                "new-password"
        )).isInstanceOf(BusinessException.class)
                .hasMessage("Código inválido ou expirado");
    }
}
