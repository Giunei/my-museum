package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.features.auth.dto.RegisterRequest;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.UserRepository;
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
        // Verificar se username já existe
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username já existe");
        }

        // Criar usuário
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();
        userRepository.save(user);

        // Criar profile e person
        userProfileService.createProfileForUser(user);
        
        // Criar token de verificação de email apenas se email fornecido
        if (request.email() != null && !request.email().trim().isEmpty()) {
            emailVerificationService.createEmailVerificationToken(user);
        }

        return user;
    }
}
