package com.giunei.my_museum.integration;

import com.giunei.my_museum.auth.service.UserProfileService;
import com.giunei.my_museum.support.AbstractIntegrationTest;
import com.giunei.my_museum.support.TestAuthHelper;
import com.giunei.my_museum.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ProfileIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUpUser() {
        TestAuthHelper.persistUser(userRepository, passwordEncoder, userProfileService, "public-profile");
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void should_returnPublicProfile_when_usernameExists() throws Exception {
        mockMvc.perform(get("/users/username/{username}/profile", "public-profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("public-profile"))
                .andExpect(jsonPath("$.visibility.canViewFullProfile").value(true));
    }

    @Test
    void should_return404_when_usernameDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/username/{username}/profile", "missing-profile"))
                .andExpect(status().isNotFound());
    }
}
