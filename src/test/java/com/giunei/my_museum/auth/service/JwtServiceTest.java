package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest extends AbstractUnitTest {

    private static final String TEST_SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LWFuZC1pbnRlZ3JhdGlvbi10ZXN0cy1vbmx5";

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMs", 3_600_000L);
        ReflectionTestUtils.setField(jwtService, "steamStateExpirationMs", 300_000L);
    }

    @Test
    void should_generateValidAccessToken_when_userIsProvided() {
        var user = TestFixtures.user(1L, "testuser");

        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void should_rejectAccessToken_when_usernameDoesNotMatch() {
        var user = TestFixtures.user(1L, "testuser");
        var otherUser = TestFixtures.user(2L, "otheruser");
        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void should_generateAndValidateSteamState_when_userIdIsProvided() {
        String state = jwtService.generateSteamState(42L);

        assertThat(jwtService.isSteamStateValid(state)).isTrue();
        assertThat(jwtService.extractUserIdFromSteamState(state)).isEqualTo(42L);
    }
}
