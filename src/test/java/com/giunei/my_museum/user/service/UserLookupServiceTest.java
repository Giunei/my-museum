package com.giunei.my_museum.user.service;

import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserLookupServiceTest extends AbstractUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserLookupService userLookupService;

    @Test
    void should_returnUser_when_usernameExists() {
        var user = TestFixtures.user(1L, "testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        var result = userLookupService.requireByUsername("testuser");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void should_throwNotFound_when_usernameDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userLookupService.requireByUsername("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }
}
