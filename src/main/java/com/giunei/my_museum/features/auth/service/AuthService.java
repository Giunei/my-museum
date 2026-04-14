package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.exceptions.InvalidPasswordOrUsernameException;
import com.giunei.my_museum.features.auth.dto.AuthResponse;
import com.giunei.my_museum.features.auth.dto.LoginRequest;
import com.giunei.my_museum.features.auth.dto.RegisterRequest;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRegistrationService userRegistrationService;
    private final EmailVerificationService emailVerificationService;

    public AuthResponse register(RegisterRequest request) {
        User user = userRegistrationService.registerUser(request);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidPasswordOrUsernameException("Usuário ou senha inválida"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordOrUsernameException("Usuário ou senha inválida");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public String verifyEmail(String token) {
        return emailVerificationService.verifyEmail(token);
    }
}
