package com.giunei.my_museum.integration;

import com.giunei.my_museum.auth.dto.LoginRequest;
import com.giunei.my_museum.auth.service.UserProfileService;
import com.giunei.my_museum.support.AbstractIntegrationTest;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AuthControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileService userProfileService;

    @BeforeEach
    void setUpUser() {
        User user = User.builder()
                .username("integration-user")
                .password(passwordEncoder.encode("password123"))
                .email("integration-user@test.com")
                .emailVerified(true)
                .build();
        userRepository.save(user);
        userProfileService.createProfileForUser(user);
    }

    @Test
    void should_returnTokens_when_loginCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest("integration-user", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void should_return401_when_loginCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest("integration-user", "wrong-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
