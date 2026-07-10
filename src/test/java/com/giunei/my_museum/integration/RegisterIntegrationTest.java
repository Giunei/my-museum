package com.giunei.my_museum.integration;

import com.giunei.my_museum.auth.dto.RegisterRequest;
import com.giunei.my_museum.support.AbstractIntegrationTest;
import com.giunei.my_museum.user.entity.Nationality;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class RegisterIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_registerUser_when_requestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "register-user",
                "password123",
                "register-user@test.com",
                Nationality.BR
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        assertThat(userRepository.findByUsername("register-user")).isPresent();
    }

    @Test
    void should_return409_when_usernameAlreadyExists() throws Exception {
        RegisterRequest first = new RegisterRequest(
                "duplicate-user",
                "password123",
                "duplicate-user@test.com",
                Nationality.BR
        );
        RegisterRequest second = new RegisterRequest(
                "duplicate-user",
                "password123",
                "another@test.com",
                Nationality.BR
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
