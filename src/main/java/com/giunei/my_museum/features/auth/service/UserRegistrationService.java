package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.exceptions.UsernameAlreadyExistsException;
import com.giunei.my_museum.features.auth.dto.RegisterRequest;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
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

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username já existe");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UsernameAlreadyExistsException("Email já cadastrado");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        userRepository.save(user);
        userProfileService.createProfileForUser(user);
        emailVerificationService.createEmailVerificationToken(user);

        return user;
    }
}
