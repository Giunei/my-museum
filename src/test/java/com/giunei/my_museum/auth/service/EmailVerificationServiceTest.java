package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.entity.EmailVerificationToken;
import com.giunei.my_museum.auth.repository.EmailVerificationTokenRepository;
import com.giunei.my_museum.common.exception.ExpiredTokenException;
import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailVerificationServiceTest extends AbstractUnitTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Test
    void should_verifyEmail_when_tokenIsValid() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmailVerified(false);
        var token = EmailVerificationToken.builder()
                .token("valid-token")
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        String message = emailVerificationService.verifyEmail("valid-token");

        assertThat(message).isEqualTo("Email verificado com sucesso!");
        assertThat(user.isEmailVerified()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void should_returnAlreadyVerifiedMessage_when_emailWasVerifiedBefore() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmailVerified(true);
        var token = EmailVerificationToken.builder()
                .token("valid-token")
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        String message = emailVerificationService.verifyEmail("valid-token");

        assertThat(message).isEqualTo("Email já verificado");
        verify(userRepository, never()).save(user);
    }

    @Test
    void should_throwExpiredToken_when_tokenIsExpired() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmailVerified(false);
        var token = EmailVerificationToken.builder()
                .token("expired-token")
                .user(user)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> emailVerificationService.verifyEmail("expired-token"))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessage("Token expirado");
    }

    @Test
    void should_throwNotFound_when_tokenDoesNotExist() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailVerificationService.verifyEmail("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Token inválido");
    }

    @Test
    void should_resendVerification_when_emailIsUnverified() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmail("testuser@test.com");
        user.setEmailVerified(false);

        when(userRepository.findByEmailIgnoreCase("testuser@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.save(org.mockito.ArgumentMatchers.any(EmailVerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String message = emailVerificationService.resendVerificationEmail("testuser@test.com");

        assertThat(message).contains("receberá um novo link");
        verify(tokenRepository).deleteByUser_Id(1L);
        verify(emailService).sendVerificationEmail(org.mockito.ArgumentMatchers.eq("testuser@test.com"),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void should_returnAlreadyVerified_when_resendingForVerifiedUser() {
        var user = TestFixtures.user(1L, "testuser");
        user.setEmail("testuser@test.com");
        user.setEmailVerified(true);

        when(userRepository.findByEmailIgnoreCase("testuser@test.com")).thenReturn(Optional.of(user));

        String message = emailVerificationService.resendVerificationEmail("testuser@test.com");

        assertThat(message).isEqualTo("Email já verificado");
        verify(tokenRepository, never()).deleteByUser_Id(org.mockito.ArgumentMatchers.anyLong());
        verify(emailService, never()).sendVerificationEmail(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }
}
