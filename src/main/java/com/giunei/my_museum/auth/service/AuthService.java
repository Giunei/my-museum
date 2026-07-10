package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.common.exception.InvalidPasswordOrUsernameException;
import com.giunei.my_museum.auth.dto.AuthResponse;
import com.giunei.my_museum.auth.dto.LoginRequest;
import com.giunei.my_museum.auth.dto.RefreshTokenRequest;
import com.giunei.my_museum.auth.dto.RegisterRequest;
import com.giunei.my_museum.auth.entity.RefreshToken;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRegistrationService userRegistrationService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    public AuthResponse register(RegisterRequest request) {
        User user = userRegistrationService.registerUser(request);
        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidPasswordOrUsernameException("Usuário ou senha inválida"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordOrUsernameException("Usuário ou senha inválida");
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken rotatedToken = refreshTokenService.rotate(request.refreshToken());
        String accessToken = jwtService.generateAccessToken(rotatedToken.getUser());

        return AuthResponse.from(
                accessToken,
                rotatedToken.getToken(),
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    public String verifyEmail(String token) {
        return emailVerificationService.verifyEmail(token);
    }

    public String requestPasswordReset(String email) {
        return passwordResetService.requestPasswordReset(email);
    }

    public String resetPassword(String email, String code, String newPassword) {
        return passwordResetService.resetPassword(email, code, newPassword);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createToken(user);

        return AuthResponse.from(
                accessToken,
                refreshToken.getToken(),
                jwtService.getAccessTokenExpirationSeconds()
        );
    }
}
