package com.giunei.my_museum.integration;

import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.auth.service.UserProfileService;
import com.giunei.my_museum.support.AbstractIntegrationTest;
import com.giunei.my_museum.support.TestAuthHelper;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class FollowFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileService userProfileService;

    private User follower;
    private User following;
    private String followerToken;

    @BeforeEach
    void setUpUsers() {
        follower = TestAuthHelper.persistUser(userRepository, passwordEncoder, userProfileService, "follower-user");
        following = TestAuthHelper.persistUser(userRepository, passwordEncoder, userProfileService, "following-user");
        followerToken = TestAuthHelper.bearerToken(jwtService, follower);
    }

    @Test
    void should_followUser_when_profilesArePublic() throws Exception {
        mockMvc.perform(post("/follow/{username}", "following-user")
                        .header("Authorization", followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void should_returnFollowStatus_when_relationshipExists() throws Exception {
        mockMvc.perform(post("/follow/{username}", "following-user")
                .header("Authorization", followerToken));

        mockMvc.perform(get("/follow/{username}/status", "following-user")
                        .header("Authorization", followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}
