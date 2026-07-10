package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.auth.entity.PasswordResetToken;
import com.giunei.my_museum.auth.repository.PasswordResetTokenRepository;
import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final String REQUEST_SUCCESS_MESSAGE =
            "Se o email estiver cadastrado, você receberá um código de confirmação em instantes.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String requestPasswordReset(String email) {
        userRepository.findByEmailIgnoreCase(email.trim()).ifPresent(user -> {
            tokenRepository.deleteByUser(user);

            String code = generateResetCode();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(code)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES))
                    .build();

            tokenRepository.save(resetToken);
            emailService.sendPasswordResetCodeEmail(user.getEmail(), code);
            log.info("Código de redefinição de senha gerado para o usuário id={}", user.getId());
        });

        return REQUEST_SUCCESS_MESSAGE;
    }

    @Transactional
    public String resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new BusinessException("Código inválido ou expirado"));

        PasswordResetToken resetToken = tokenRepository.findByToken(code.trim())
                .filter(token -> token.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Código inválido ou expirado"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new BusinessException("Código inválido ou expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.deleteByUser(user);

        return "Senha redefinida com sucesso!";
    }

    private String generateResetCode() {
        int code = 100_000 + new SecureRandom().nextInt(900_000);
        return String.valueOf(code);
    }
}
