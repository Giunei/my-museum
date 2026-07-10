package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.user.entity.Person;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.profile.repository.ProfileRepository;
import com.giunei.my_museum.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final ProfileRepository profileRepository;
    private final PersonRepository personRepository;

    @Transactional
    public void createProfileForUser(User user) {
        profileRepository.save(Profile.builder().user(user).build());
        personRepository.save(Person.builder().user(user).build());
    }
}
