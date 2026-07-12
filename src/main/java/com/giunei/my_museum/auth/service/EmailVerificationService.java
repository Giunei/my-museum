package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.entity.EmailVerificationToken;
import com.giunei.my_museum.auth.repository.EmailVerificationTokenRepository;
import com.giunei.my_museum.common.exception.ExpiredTokenException;
import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String GENERIC_RESEND_MESSAGE =
            "Se o email estiver cadastrado e ainda não verificado, você receberá um novo link em instantes.";

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public String verifyEmail(String token) {
        EmailVerificationToken verification = tokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token inválido"));

        if (verification.getUser().isEmailVerified()) {
            return "Email já verificado";
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Token expirado");
        }

        User user = verification.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return "Email verificado com sucesso!";
    }

    /**
     * Persists the verification token. Call {@link #sendVerificationEmail(User, String)}
     * after the surrounding transaction commits.
     */
    @Transactional
    public String createEmailVerificationToken(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmailVerified(true);
            userRepository.save(user);
            return null;
        }

        EmailVerificationToken verification = EmailVerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(verification);
        return verification.getToken();
    }

    public void sendVerificationEmail(User user, String token) {
        if (token == null || user.getEmail() == null || user.getEmail().isBlank()) {
            return;
        }
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    /**
     * Creates a fresh verification token and sends the email.
     * Unknown emails get a generic response to avoid account enumeration.
     */
    @Transactional
    public String resendVerificationEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim())
                .map(this::resendForUser)
                .orElse(GENERIC_RESEND_MESSAGE);
    }

    private String resendForUser(User user) {
        if (user.isEmailVerified()) {
            return "Email já verificado";
        }

        tokenRepository.deleteByUser_Id(user.getId());
        String token = createEmailVerificationToken(user);
        sendVerificationEmail(user, token);
        return GENERIC_RESEND_MESSAGE;
    }
}
