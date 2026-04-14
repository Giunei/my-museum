package com.giunei.my_museum.features.user.profile.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.Person;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.follow.service.FollowService;
import com.giunei.my_museum.features.user.profile.ProfileTheme;
import com.giunei.my_museum.features.user.profile.dto.CompleteProfileRequest;
import com.giunei.my_museum.features.user.profile.dto.CompleteProfileResponse;
import com.giunei.my_museum.features.user.profile.dto.ProfileResponse;
import com.giunei.my_museum.features.user.profile.entity.Profile;
import com.giunei.my_museum.features.user.profile.repository.ProfileRepository;
import com.giunei.my_museum.features.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Service
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileService {

    private final FollowService followService;
    private final PersonRepository personRepository;

    public ProfileResponse getMyProfile() {
        User user = SecurityUtils.getAuthenticatedUser();
        Person person = user.getPerson();
        Profile profile = user.getProfile();

        int followers = followService.getFollowersCount(user);

        return new ProfileResponse(
                user.getId(),
                person.getName(),
                profile.getProfileImageUrl(),
                followers,
                profile.getBio(),
                person.getNationality() != null ? person.getNationality().name() : null,
                person.getGender() != null ? person.getGender().name() : null
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

        user.setOnboardingCompleted(true);

        return CompleteProfileResponse.from(user);
    }
}
