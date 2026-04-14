package com.giunei.my_museum.core.config;

import com.giunei.my_museum.exceptions.NoAuthenticatedException;
import com.giunei.my_museum.features.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new NoAuthenticatedException("No authenticated user");
        }

        return (User) authentication.getPrincipal();
    }
}
