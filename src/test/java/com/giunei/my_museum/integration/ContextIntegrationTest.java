package com.giunei.my_museum.integration;

import com.giunei.my_museum.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class ContextIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void should_loadApplicationContext_when_testProfileIsActive() {
        assertThat(applicationContext).isNotNull();
    }
}
