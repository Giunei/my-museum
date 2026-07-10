package com.giunei.my_museum.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giunei.my_museum.auth.service.EmailService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci11bml0LWFuZC1pbnRlZ3JhdGlvbi10ZXN0cy1vbmx5"
})
@Import(TestCacheConfig.class)
public abstract class AbstractIntegrationTest {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    protected EmailService emailService;
}