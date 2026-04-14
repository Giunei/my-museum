package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.auth.entity.EmailVerificationToken;
import com.giunei.my_museum.features.auth.repository.EmailVerificationTokenRepository;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public String verifyEmail(String token) {
        EmailVerificationToken verification = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (verification.getUser().isEmailVerified()) {
            return "Email já verificado";
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        User user = verification.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Remover o token após verificação
//        tokenRepository.delete(verification);

        return "Email verificado com sucesso!";
    }

    @Transactional
    public void createEmailVerificationToken(User user) {
        EmailVerificationToken verification = EmailVerificationToken.builder()
                .token(java.util.UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(verification);

        // Enviar email de verificação
//        emailService.sendVerificationEmail(user.getEmail(), verification.getToken());
    }
}
