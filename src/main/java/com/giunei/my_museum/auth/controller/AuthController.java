package com.giunei.my_museum.auth.controller;

import com.giunei.my_museum.auth.dto.AuthResponse;
import com.giunei.my_museum.auth.dto.ForgotPasswordRequest;
import com.giunei.my_museum.auth.dto.LoginRequest;
import com.giunei.my_museum.auth.dto.MessageResponse;
import com.giunei.my_museum.auth.dto.RefreshTokenRequest;
import com.giunei.my_museum.auth.dto.RegisterRequest;
import com.giunei.my_museum.auth.dto.ResendVerificationRequest;
import com.giunei.my_museum.auth.dto.ResetPasswordRequest;
import com.giunei.my_museum.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return service.refresh(request);
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        return service.verifyEmail(token);
    }

    @PostMapping("/resend-verification")
    public MessageResponse resendVerification(@RequestBody @Valid ResendVerificationRequest request) {
        return new MessageResponse(service.resendVerificationEmail(request.email()));
    }

    @PostMapping("/forgot-password")
    public MessageResponse forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        return new MessageResponse(service.requestPasswordReset(request.email()));
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return new MessageResponse(service.resetPassword(
                request.email(),
                request.code(),
                request.newPassword()
        ));
    }

    @PostMapping("/logout")
    public void logout() {
        // JWT tokens are stateless, logout is handled client-side by discarding the token
    }
}
