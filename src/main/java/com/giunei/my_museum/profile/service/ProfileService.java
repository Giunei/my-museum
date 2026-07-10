package com.giunei.my_museum.profile.service;

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
import com.giunei.my_museum.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final FollowService followService;
    private final PersonRepository personRepository;
    private final ProfileRepository profileRepository;
    private final UserMediaRepository userMediaRepository;
    private final UserLookupService userLookupService;
    private final ProfileAccessService profileAccessService;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = SecurityUtils.getAuthenticatedUser();
        return buildProfileResponse(user, user);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        User user = userLookupService.requireByUsername(username);
        User viewer = SecurityUtils.getAuthenticatedUserOrNull();
        return buildProfileResponse(user, viewer);
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
    public CompleteProfileResponse completeProfile(CompleteProfileRequest request) {
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

        user.setOnboardingCompleted(true);

        return CompleteProfileResponse.from(user);
    }

    private ProfileResponse buildProfileResponse(User user, User viewer) {
        Person person = user.getPerson();
        Profile profile = user.getProfile();
        ProfileVisibilityInfo visibility = profileAccessService.resolveVisibility(user, viewer);
        boolean canViewFullProfile = visibility.canViewFullProfile();

        int followers = followService.getFollowersCount(user);
        int following = followService.getFollowingCount(user);
        Long totalItems = canViewFullProfile ? userMediaRepository.countByUser(user) : null;

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
                visibility
        );
    }
}
