package com.giunei.my_museum.integration;

import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.auth.service.UserProfileService;
import com.giunei.my_museum.social.comment.dto.CreateProfileCommentRequest;
import com.giunei.my_museum.support.AbstractIntegrationTest;
import com.giunei.my_museum.support.TestAuthHelper;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ProfileCommentIntegrationTest extends AbstractIntegrationTest {

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

    private User owner;
    private User author;
    private String authorToken;

    @BeforeEach
    void setUpUsers() {
        owner = TestAuthHelper.persistUser(userRepository, passwordEncoder, userProfileService, "profile-owner");
        author = TestAuthHelper.persistUser(userRepository, passwordEncoder, userProfileService, "comment-author");
        authorToken = TestAuthHelper.bearerToken(jwtService, author);
    }

    @Test
    void should_returnEmptyPage_when_profileHasNoComments() throws Exception {
        mockMvc.perform(get("/profile/{username}/comments", "profile-owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void should_createComment_when_userIsAuthenticated() throws Exception {
        CreateProfileCommentRequest request = new CreateProfileCommentRequest("Ótimo perfil!");

        mockMvc.perform(post("/profile/{username}/comments", "profile-owner")
                        .header("Authorization", authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Ótimo perfil!"))
                .andExpect(jsonPath("$.authorName").value("comment-author"));
    }
}
