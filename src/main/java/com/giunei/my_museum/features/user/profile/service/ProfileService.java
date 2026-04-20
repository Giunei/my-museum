package com.giunei.my_museum.features.user.profile.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.user.entity.Person;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.follow.service.FollowService;
import com.giunei.my_museum.features.user.profile.ProfileTheme;
import com.giunei.my_museum.features.user.profile.dto.*;
import com.giunei.my_museum.features.user.profile.entity.Profile;
import com.giunei.my_museum.features.user.profile.repository.ProfileRepository;
import com.giunei.my_museum.features.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileService {

    private final FollowService followService;
    private final PersonRepository personRepository;
    private final ProfileRepository profileRepository;

    public ProfileResponse getMyProfile() {
        User user = SecurityUtils.getAuthenticatedUser();
        Person person = user.getPerson();
        Profile profile = user.getProfile();

        int followers = followService.getFollowersCount(user);

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getUsername()
        );

        ProfileInfo profileInfo = new ProfileInfo(
                profile.getProfileImageUrl(),
                profile.getBio()
        );

        PersonInfo personInfo = new PersonInfo(
                person.getName(),
                person.getNationality() != null ? person.getNationality().name() : null,
                person.getGender() != null ? person.getGender().name() : null
        );

        SocialInfo socialInfo = new SocialInfo(followers);

        // TODO: Implementar busca de highlights quando disponível
        HighlightInfo highlightInfo = new HighlightInfo(List.of());

        // TODO: Implementar gamificação quando disponível
        GamificationInfo gamificationInfo = new GamificationInfo(1, 0);

        return new ProfileResponse(
                userInfo,
                profileInfo,
                personInfo,
                socialInfo,
                highlightInfo,
                gamificationInfo
        );
    }

    @Transactional
    public void updateTheme(ProfileTheme theme) {
        User user = SecurityUtils.getAuthenticatedUser();

        user.getProfile().setTheme(theme);
    }

    @Transactional
    public CompleteProfileResponse completeProfile(CompleteProfileRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        Person person = user.getPerson();

        person.setName(request.name());
        person.setNationality(request.nationality());
        person.setGender(request.gender());
        personRepository.save(person);

        Profile profile = user.getProfile();
        profile.setTheme(request.theme());
        profileRepository.save(profile);

        user.setOnboardingCompleted(true);

        return CompleteProfileResponse.from(user);
    }
}
