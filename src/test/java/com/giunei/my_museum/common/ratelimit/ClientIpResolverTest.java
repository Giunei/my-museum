package com.giunei.my_museum.common.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ClientIpResolverTest {

    @Test
    void prefersFirstXForwardedForHop() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");
        request.setRemoteAddr("127.0.0.1");

        assertThat(ClientIpResolver.resolve(request)).isEqualTo("203.0.113.10");
    }

    @Test
    void fallsBackToRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("198.51.100.20");

        assertThat(ClientIpResolver.resolve(request)).isEqualTo("198.51.100.20");
    }
}
