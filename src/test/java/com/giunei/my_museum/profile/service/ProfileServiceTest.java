package com.giunei.my_museum.profile.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.profile.dto.ProfileResponse;
import com.giunei.my_museum.profile.repository.ProfileRepository;
import com.giunei.my_museum.social.service.FollowService;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.repository.PersonRepository;
import com.giunei.my_museum.user.service.UserLookupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ProfileServiceTest extends AbstractUnitTest {

    @Mock
    private FollowService followService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserMediaRepository userMediaRepository;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private ProfileAccessService profileAccessService;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void should_returnPublicProfile_when_profileIsVisible() {
        var owner = TestFixtures.userWithProfileAndPerson(1L, "owner", false);
        var visibility = new com.giunei.my_museum.profile.dto.ProfileVisibilityInfo(
                false,
                true,
                com.giunei.my_museum.profile.FollowRelationStatus.NONE
        );

        when(userLookupService.requireByUsername("owner")).thenReturn(owner);
        when(profileAccessService.resolveVisibility(owner, null)).thenReturn(visibility);
        when(followService.getFollowersCount(owner)).thenReturn(10);
        when(followService.getFollowingCount(owner)).thenReturn(5);
        when(userMediaRepository.countByUser(owner)).thenReturn(42L);
        when(userMediaRepository.countByUserAndRatingIsNotNull(owner)).thenReturn(6L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUserOrNull).thenReturn(null);

            ProfileResponse response = profileService.getProfileByUsername("owner");
            assertThat(response.user().username()).isEqualTo("owner");
            assertThat(response.social().followers()).isEqualTo(10);
            assertThat(response.totalItems()).isEqualTo(42L);
            assertThat(response.ratingsCount()).isEqualTo(6L);
        }
    }

    @Test
    void should_hideCollectionCount_when_privateProfileIsRestricted() {
        var owner = TestFixtures.userWithProfileAndPerson(1L, "owner", true);
        var visibility = new com.giunei.my_museum.profile.dto.ProfileVisibilityInfo(
                true,
                false,
                com.giunei.my_museum.profile.FollowRelationStatus.NONE
        );

        when(userLookupService.requireByUsername("owner")).thenReturn(owner);
        when(profileAccessService.resolveVisibility(owner, null)).thenReturn(visibility);
        when(followService.getFollowersCount(owner)).thenReturn(3);
        when(followService.getFollowingCount(owner)).thenReturn(1);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUserOrNull).thenReturn(null);

            ProfileResponse response = profileService.getProfileByUsername("owner");

            assertThat(response.totalItems()).isNull();
            assertThat(response.ratingsCount()).isNull();
            assertThat(response.visibility().canViewFullProfile()).isFalse();
        }
    }
}
