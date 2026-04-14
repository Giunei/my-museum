package com.giunei.my_museum.features.auth.service;

import com.giunei.my_museum.features.museum.Museum;
import com.giunei.my_museum.features.museum.MuseumRepository;
import com.giunei.my_museum.features.user.entity.Person;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.profile.entity.Profile;
import com.giunei.my_museum.features.user.profile.repository.ProfileRepository;
import com.giunei.my_museum.features.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final ProfileRepository profileRepository;
    private final PersonRepository personRepository;
    private final MuseumRepository museumRepository;

    @Transactional
    public void createProfileForUser(User user) {
        // Criar profile
        Profile profile = Profile.builder()
                .user(user)
                .build();
        profileRepository.save(profile);

        // Criar person
        Person person = Person.builder()
                .user(user)
                .build();
        personRepository.save(person);

        // Criar museum
        Museum museum = Museum.builder()
                .user(user)
                .build();
        museumRepository.save(museum);
    }
}
