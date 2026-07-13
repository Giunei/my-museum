package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.dto.RegisterRequest;
import com.giunei.my_museum.common.exception.UsernameAlreadyExistsException;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;
    private final EmailVerificationService emailVerificationService;

    public record RegistrationResult(User user, String verificationToken) {}

    @Transactional
    public RegistrationResult registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username já existe");
        }

        String email = blankToNull(request.email());
        if (email != null && userRepository.existsByEmail(email)) {
            throw new UsernameAlreadyExistsException("Email já cadastrado");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(email)
                .build();

        userRepository.save(user);
        userProfileService.createProfileForUser(user);
        String verificationToken = emailVerificationService.createEmailVerificationToken(user);

        return new RegistrationResult(user, verificationToken);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
