package com.giunei.my_museum.media.service;

import com.giunei.my_museum.achievement.service.AchievementService;
import com.giunei.my_museum.achievement.service.UserGoalService;
import com.giunei.my_museum.common.exception.DuplicateMediaException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.UserMediaRequest;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.MediaCollectionRepository;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class UserMediaServiceTest extends AbstractUnitTest {

    @Mock
    private UserMediaRepository repository;

    @Mock
    private UserGoalService goalService;

    @Mock
    private AchievementService achievementService;

    @Mock
    private MediaCollectionRepository collectionRepository;

    @InjectMocks
    private UserMediaService userMediaService;

    @Test
    void should_throwDuplicateMedia_when_externalIdAlreadyExists() {
        var user = TestFixtures.user(1L, "testuser");
        var request = new UserMediaRequest(
                "book-123",
                MediaType.BOOK,
                "Duna",
                "https://thumb",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(repository.existsByExternalIdAndUser("book-123", user)).thenReturn(true);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            assertThatThrownBy(() -> userMediaService.create(request))
                    .isInstanceOf(DuplicateMediaException.class)
                    .hasMessage("Você já adicionou este item à sua biblioteca");
        }
    }
}
