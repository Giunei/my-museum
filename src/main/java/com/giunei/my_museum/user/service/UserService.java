package com.giunei.my_museum.user.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.common.exception.AccessDeniedException;
import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.dto.UserResponse;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public UserResponse findById(Long id) {
        User authenticatedUser = SecurityUtils.getAuthenticatedUser();

        if (!authenticatedUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.isOnboardingCompleted()
        );
    }
}
