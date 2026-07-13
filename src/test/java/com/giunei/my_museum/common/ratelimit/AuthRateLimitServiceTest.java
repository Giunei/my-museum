package com.giunei.my_museum.common.ratelimit;

import com.giunei.my_museum.common.exception.RateLimitExceededException;
import com.giunei.my_museum.support.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthRateLimitServiceTest extends AbstractUnitTest {

    private AuthRateLimitService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        ObjectProvider<io.github.bucket4j.distributed.proxy.ProxyManager<String>> proxyManager =
                mock(ObjectProvider.class);
        when(proxyManager.getIfAvailable()).thenReturn(null);
        service = new AuthRateLimitService(proxyManager);
    }

    @Test
    void allowsFiveLoginAttemptsPerMinute() {
        String ip = "1.2.3.4";
        for (int i = 0; i < 5; i++) {
            assertThatCode(() -> service.checkLogin(ip)).doesNotThrowAnyException();
        }

        assertThatThrownBy(() -> service.checkLogin(ip))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("login");
    }

    @Test
    void allowsThreeRegisterAttemptsPerHour() {
        String ip = "5.6.7.8";
        for (int i = 0; i < 3; i++) {
            assertThatCode(() -> service.checkRegister(ip)).doesNotThrowAnyException();
        }

        assertThatThrownBy(() -> service.checkRegister(ip))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("cadastros");
    }

    @Test
    void isolatesBucketsByIp() {
        assertThatCode(() -> {
            for (int i = 0; i < 5; i++) {
                service.checkLogin("10.0.0.1");
            }
            service.checkLogin("10.0.0.2");
        }).doesNotThrowAnyException();
    }
}
