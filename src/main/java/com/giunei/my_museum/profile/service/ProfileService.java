package com.giunei.my_museum.profile.service;

import com.giunei.my_museum.auth.entity.RefreshToken;
import com.giunei.my_museum.auth.repository.EmailVerificationTokenRepository;
import com.giunei.my_museum.auth.service.EmailVerificationService;
import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.auth.service.RefreshTokenService;
import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.common.exception.EmailNotVerifiedException;
import com.giunei.my_museum.common.exception.UsernameAlreadyExistsException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.profile.ProfileTheme;
import com.giunei.my_museum.profile.dto.*;
import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.profile.repository.ProfileRepository;
import com.giunei.my_museum.social.service.FollowService;
import com.giunei.my_museum.user.entity.Person;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.PersonRepository;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileService {

    public static final int USERNAME_CHANGE_COOLDOWN_DAYS = 15;

    private final FollowService followService;
    private final PersonRepository personRepository;
    private final ProfileRepository profileRepository;
    private final UserMediaRepository userMediaRepository;
    private final UserLookupService userLookupService;
    private final ProfileAccessService profileAccessService;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public record ProfileUpdateResult(
            CompleteProfileResponse response,
            User user,
            String verificationToken
    ) {
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = SecurityUtils.getAuthenticatedUser();
        return buildProfileResponse(user, user, true);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        User user = userLookupService.requireByUsername(username);
        User viewer = SecurityUtils.getAuthenticatedUserOrNull();
        return buildProfileResponse(user, viewer, false);
    }

    @Transactional
    public void updateTheme(ProfileTheme theme) {
        User user = SecurityUtils.getAuthenticatedUser();
        Profile profile = user.getProfile();
        profile.setTheme(theme);
        profileRepository.save(profile);
    }

    @Transactional
    public void updatePrivacy(boolean privateProfile) {
        User user = SecurityUtils.getAuthenticatedUser();
        Profile profile = user.getProfile();
        profile.setPrivateProfile(privateProfile);
        profileRepository.save(profile);
    }

    @Transactional
    public ProfileUpdateResult completeProfile(CompleteProfileRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        Person person = user.getPerson();

        person.setName(request.name());
        person.setNationality(request.nationality());
        person.setGender(request.gender());
        person.setBirthDate(request.birthDate());
        personRepository.save(person);

        Profile profile = user.getProfile();
        profile.setBio(request.bio());
        profileRepository.save(profile);

        boolean usernameChanged = applyUsernameChange(user, request.username());
        String verificationToken = applyEmailChange(user, request.email());

        user.setOnboardingCompleted(true);
        userRepository.save(user);

        String accessToken = null;
        String refreshTokenValue = null;
        Long expiresIn = null;
        if (usernameChanged) {
            accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createToken(user);
            refreshTokenValue = refreshToken.getToken();
            expiresIn = jwtService.getAccessTokenExpirationSeconds();
        }

        CompleteProfileResponse response = new CompleteProfileResponse(
                person.getName(),
                person.getNationality(),
                person.getGender(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                verificationToken != null,
                nextUsernameChangeAvailableAt(user),
                accessToken,
                refreshTokenValue,
                expiresIn
        );

        return new ProfileUpdateResult(response, user, verificationToken);
    }

    private boolean applyUsernameChange(User user, String requestedUsername) {
        if (requestedUsername == null || requestedUsername.isBlank()) {
            return false;
        }

        String trimmed = requestedUsername.trim();
        if (trimmed.equals(user.getUsername())) {
            return false;
        }

        LocalDateTime nextAllowed = nextUsernameChangeAvailableAt(user);
        if (nextAllowed != null) {
            throw new BusinessException(
                    "Username só pode ser alterado a cada "
                            + USERNAME_CHANGE_COOLDOWN_DAYS
                            + " dias. Próxima alteração disponível em "
                            + nextAllowed
            );
        }

        if (userRepository.existsByUsername(trimmed)) {
            throw new UsernameAlreadyExistsException("Username já existe");
        }

        user.setUsername(trimmed);
        user.setUsernameChangedAt(LocalDateTime.now());
        return true;
    }

    private String applyEmailChange(User user, String requestedEmail) {
        if (requestedEmail == null || requestedEmail.isBlank()) {
            return null;
        }

        String trimmed = requestedEmail.trim();
        String currentEmail = user.getEmail();
        boolean hadEmail = currentEmail != null && !currentEmail.isBlank();

        if (hadEmail && currentEmail.equalsIgnoreCase(trimmed)) {
            return null;
        }

        if (hadEmail && !user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Confirme o email atual antes de alterá-lo"
            );
        }

        userRepository.findByEmailIgnoreCase(trimmed).ifPresent(existing -> {
            if (!existing.getId().equals(user.getId())) {
                throw new UsernameAlreadyExistsException("Email já cadastrado");
            }
        });

        user.setEmail(trimmed);
        user.setEmailVerified(false);
        emailVerificationTokenRepository.deleteByUser_Id(user.getId());
        return emailVerificationService.createEmailVerificationToken(user);
    }

    static LocalDateTime nextUsernameChangeAvailableAt(User user) {
        LocalDateTime lastChange = user.getUsernameChangedAt();
        if (lastChange == null) {
            return null;
        }
        LocalDateTime next = lastChange.plusDays(USERNAME_CHANGE_COOLDOWN_DAYS);
        return next.isAfter(LocalDateTime.now()) ? next : null;
    }

    private ProfileResponse buildProfileResponse(User user, User viewer, boolean includeAccount) {
        Person person = user.getPerson();
        Profile profile = user.getProfile();
        ProfileVisibilityInfo visibility = profileAccessService.resolveVisibility(user, viewer);
        boolean canViewFullProfile = visibility.canViewFullProfile();

        int followers = followService.getFollowersCount(user);
        int following = followService.getFollowingCount(user);
        Long totalItems = canViewFullProfile ? userMediaRepository.countByUser(user) : null;
        Long ratingsCount = canViewFullProfile
                ? userMediaRepository.countByUserAndCompletedTrueAndRatingIsNotNull(user)
                : null;

        AccountSettingsInfo account = null;
        if (includeAccount) {
            account = new AccountSettingsInfo(
                    user.getEmail(),
                    user.isEmailVerified(),
                    nextUsernameChangeAvailableAt(user)
            );
        }

        return new ProfileResponse(
                new UserInfo(user.getId(), user.getUsername()),
                new ProfileInfo(profile.getProfileImageUrl(), null, profile.getBio(), profile.getTheme()),
                new PersonInfo(
                        person.getName(),
                        person.getNationality() != null ? person.getNationality().name() : null,
                        person.getGender() != null ? person.getGender().name() : null,
                        person.getBirthDate()
                ),
                new SocialInfo(followers, following),
                totalItems,
                ratingsCount,
                visibility,
                account
        );
    }
}
