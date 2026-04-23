package com.giunei.my_museum.features.user.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.exceptions.AccessDeniedException;
import com.giunei.my_museum.exceptions.NotFoundException;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.dto.UserReponse;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public UserReponse findById(Long id) {
        User authenticatedUser = SecurityUtils.getAuthenticatedUser();

        if (!authenticatedUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return new UserReponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.isOnboardingCompleted()
        );
    }
}
