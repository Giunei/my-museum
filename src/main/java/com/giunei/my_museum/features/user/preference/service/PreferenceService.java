package com.giunei.my_museum.features.user.preference.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.preference.dto.PreferenceRequest;
import com.giunei.my_museum.features.user.preference.dto.PreferenceResponse;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.preference.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository repository;
    private final PreferenceBuilder builder;
    private final PreferenceMapper mapper;
    private final UserRepository userRepository;

    @Transactional
    public void savePreferences(PreferenceRequest request) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();

        User user = userRepository.findById(userId)
                .orElseThrow();

        List<Preference> preferences = builder.build(user, request);

        repository.saveAll(preferences);

        user.setOnboardingCompleted(true);
    }

    @Transactional
    public void updatePreferences(PreferenceRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        repository.deleteByUser(user);

        List<Preference> preferences = builder.build(user, request);
        repository.saveAll(preferences);
    }

    public PreferenceResponse getMyPreferences() {
        User user = SecurityUtils.getAuthenticatedUser();

        List<Preference> prefs = repository.findByUser(user);

        return mapper.toResponse(prefs);
    }
}
