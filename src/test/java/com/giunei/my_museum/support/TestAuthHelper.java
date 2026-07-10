package com.giunei.my_museum.support;

import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.auth.service.UserProfileService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class TestAuthHelper {

    private TestAuthHelper() {
    }

    public static String bearerToken(JwtService jwtService, User user) {
        return "Bearer " + jwtService.generateAccessToken(user);
    }

    public static User persistUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserProfileService userProfileService,
            String username
    ) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .email(username + "@test.com")
                .emailVerified(true)
                .build();
        userRepository.saveAndFlush(user);
        userProfileService.createProfileForUser(user);
        return userRepository.findByUsername(username).orElseThrow();
    }
}
