package com.giunei.my_museum.preference.service;

import com.giunei.my_museum.preference.dto.PreferenceResponse;
import com.giunei.my_museum.preference.entity.Preference;
import com.giunei.my_museum.preference.entity.PreferenceType;
import com.giunei.my_museum.preference.repository.PreferenceRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PreferenceServiceTest extends AbstractUnitTest {

    @Mock
    private PreferenceRepository repository;

    @Mock
    private PreferenceBuilder builder;

    @Mock
    private PreferenceMapper mapper;

    @Mock
    private com.giunei.my_museum.user.repository.UserRepository userRepository;

    @InjectMocks
    private PreferenceService preferenceService;

    @Test
    void should_returnMappedPreferences_when_userHasPreferences() {
        var user = TestFixtures.user(1L, "testuser");
        var preferences = List.of(
                Preference.builder().user(user).type(PreferenceType.BOOK).value("Fiction").build()
        );
        var response = new PreferenceResponse(List.of("Fiction"), List.of(), List.of(), List.of());

        when(repository.findByUser(user)).thenReturn(preferences);
        when(mapper.toResponse(preferences)).thenReturn(response);

        PreferenceResponse result = preferenceService.getPreferences(user);

        assertThat(result.bookGenres()).containsExactly("Fiction");
        assertThat(result.movieGenres()).isEmpty();
    }
}
