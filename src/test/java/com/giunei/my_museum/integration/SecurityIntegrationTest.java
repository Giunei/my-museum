package com.giunei.my_museum.integration;

import com.giunei.my_museum.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return401_when_protectedEndpointHasNoToken() throws Exception {
        mockMvc.perform(get("/follow/requests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return404_when_usernameDoesNotExist() throws Exception {
        mockMvc.perform(get("/profile/{username}/comments", "missing-user"))
                .andExpect(status().isNotFound());
    }
}
