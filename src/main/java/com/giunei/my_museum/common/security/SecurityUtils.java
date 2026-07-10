package com.giunei.my_museum.common.security;

import com.giunei.my_museum.common.exception.NoAuthenticatedException;
import com.giunei.my_museum.user.entity.User;
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

    public static User getAuthenticatedUserOrNull() {
        try {
            return getAuthenticatedUser();
        } catch (NoAuthenticatedException e) {
            return null;
        }
    }
}
