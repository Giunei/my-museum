package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.dto.RegisterRequest;
import com.giunei.my_museum.common.exception.UsernameAlreadyExistsException;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.user.entity.Nationality;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRegistrationServiceTest extends AbstractUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    void should_registerUser_when_requestIsValid() {
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "password123",
                "newuser@test.com",
                Nationality.BR
        );

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailVerificationService.createEmailVerificationToken(any(User.class))).thenReturn("verify-token");

        UserRegistrationService.RegistrationResult result = userRegistrationService.registerUser(request);
        User user = result.user();

        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getEmail()).isEqualTo("newuser@test.com");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(result.verificationToken()).isEqualTo("verify-token");
        verify(userProfileService).createProfileForUser(user);
        verify(emailVerificationService).createEmailVerificationToken(user);
    }

    @Test
    void should_throwUsernameAlreadyExists_when_usernameIsTaken() {
        RegisterRequest request = new RegisterRequest(
                "existing",
                "password123",
                "existing@test.com",
                Nationality.BR
        );

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.registerUser(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("Username já existe");

        verify(userRepository, never()).save(any());
    }

    @Test
    void should_throwUsernameAlreadyExists_when_emailIsTaken() {
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "password123",
                "taken@test.com",
                Nationality.BR
        );

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.registerUser(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("Email já cadastrado");

        verify(userRepository, never()).save(any());
    }
}
