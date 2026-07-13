package com.giunei.my_museum.profile.service;

import com.giunei.my_museum.auth.repository.EmailVerificationTokenRepository;
import com.giunei.my_museum.auth.service.EmailVerificationService;
import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.auth.service.RefreshTokenService;
import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.common.exception.EmailNotVerifiedException;
import com.giunei.my_museum.common.exception.UsernameAlreadyExistsException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.profile.dto.CompleteProfileRequest;
import com.giunei.my_museum.profile.dto.ProfileResponse;
import com.giunei.my_museum.profile.repository.ProfileRepository;
import com.giunei.my_museum.social.service.FollowService;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.PersonRepository;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.service.UserLookupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

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
        when(userMediaRepository.countByUserAndCompletedTrueAndRatingIsNotNull(owner)).thenReturn(6L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUserOrNull).thenReturn(null);

            ProfileResponse response = profileService.getProfileByUsername("owner");
            assertThat(response.user().username()).isEqualTo("owner");
            assertThat(response.social().followers()).isEqualTo(10);
            assertThat(response.totalItems()).isEqualTo(42L);
            assertThat(response.ratingsCount()).isEqualTo(6L);
            assertThat(response.account()).isNull();
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

    @Test
    void should_includeAccountSettings_when_gettingOwnProfile() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);
        var visibility = new com.giunei.my_museum.profile.dto.ProfileVisibilityInfo(
                false,
                true,
                com.giunei.my_museum.profile.FollowRelationStatus.NONE
        );

        when(profileAccessService.resolveVisibility(user, user)).thenReturn(visibility);
        when(followService.getFollowersCount(user)).thenReturn(0);
        when(followService.getFollowingCount(user)).thenReturn(0);
        when(userMediaRepository.countByUser(user)).thenReturn(0L);
        when(userMediaRepository.countByUserAndCompletedTrueAndRatingIsNotNull(user)).thenReturn(0L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            ProfileResponse response = profileService.getMyProfile();

            assertThat(response.account()).isNotNull();
            assertThat(response.account().email()).isEqualTo("me@test.com");
            assertThat(response.account().emailVerified()).isTrue();
            assertThat(response.account().nextUsernameChangeAvailableAt()).isNull();
        }
    }

    @Test
    void should_rejectEmailChange_when_currentEmailNotVerified() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);
        user.setEmailVerified(false);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            assertThatThrownBy(() -> profileService.completeProfile(
                    new CompleteProfileRequest("Name", null, null, null, "bio", null, "new@test.com")
            )).isInstanceOf(EmailNotVerifiedException.class);
        }
    }

    @Test
    void should_allowAddingFirstEmail_and_sendVerification() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);
        user.setEmail(null);
        user.setEmailVerified(false);

        when(userRepository.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());
        when(emailVerificationService.createEmailVerificationToken(user)).thenReturn("token-1");
        when(userRepository.save(user)).thenReturn(user);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            var result = profileService.completeProfile(
                    new CompleteProfileRequest("Name", null, null, null, "bio", null, "new@test.com")
            );

            assertThat(result.verificationToken()).isEqualTo("token-1");
            assertThat(result.response().emailVerificationSent()).isTrue();
            assertThat(user.getEmail()).isEqualTo("new@test.com");
            assertThat(user.isEmailVerified()).isFalse();
            verify(emailVerificationTokenRepository).deleteByUser_Id(1L);
        }
    }

    @Test
    void should_changeEmail_when_currentEmailIsVerified() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);
        user.setEmailVerified(true);

        when(userRepository.findByEmailIgnoreCase("other@test.com")).thenReturn(Optional.empty());
        when(emailVerificationService.createEmailVerificationToken(user)).thenReturn("token-2");
        when(userRepository.save(user)).thenReturn(user);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            var result = profileService.completeProfile(
                    new CompleteProfileRequest("Name", null, null, null, "bio", null, "other@test.com")
            );

            assertThat(result.response().email()).isEqualTo("other@test.com");
            assertThat(result.response().emailVerified()).isFalse();
            assertThat(result.verificationToken()).isEqualTo("token-2");
        }
    }

    @Test
    void should_rejectUsernameChange_withinCooldown() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);
        user.setUsernameChangedAt(LocalDateTime.now().minusDays(2));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            assertThatThrownBy(() -> profileService.completeProfile(
                    new CompleteProfileRequest("Name", null, null, null, "bio", "newname", null)
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("15 dias");

            verify(userRepository, never()).existsByUsername(any());
        }
    }

    @Test
    void should_changeUsername_and_issueTokens_when_cooldownElapsed() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);
        user.setUsernameChangedAt(LocalDateTime.now().minusDays(16));

        when(userRepository.existsByUsername("newname")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(3600L);

        var refresh = com.giunei.my_museum.auth.entity.RefreshToken.builder().build();
        refresh.setToken("refresh");
        when(refreshTokenService.createToken(user)).thenReturn(refresh);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            var result = profileService.completeProfile(
                    new CompleteProfileRequest("Name", null, null, null, "bio", "newname", null)
            );

            assertThat(user.getUsername()).isEqualTo("newname");
            assertThat(result.response().accessToken()).isEqualTo("access");
            assertThat(result.response().refreshToken()).isEqualTo("refresh");
            assertThat(result.response().expiresIn()).isEqualTo(3600L);
            assertThat(result.response().nextUsernameChangeAvailableAt()).isNotNull();
        }
    }

    @Test
    void should_rejectTakenUsername() {
        var user = TestFixtures.userWithProfileAndPerson(1L, "me", false);

        when(userRepository.existsByUsername("taken")).thenReturn(true);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);

            assertThatThrownBy(() -> profileService.completeProfile(
                    new CompleteProfileRequest("Name", null, null, null, "bio", "taken", null)
            )).isInstanceOf(UsernameAlreadyExistsException.class);
        }
    }
}
