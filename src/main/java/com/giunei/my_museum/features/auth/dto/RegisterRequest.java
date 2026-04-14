package com.giunei.my_museum.features.auth.dto;

import com.giunei.my_museum.features.user.entity.Nationality;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        String username,

        @NotBlank @Size(min = 6)
        String password,

        @Email
        String email,

        Nationality nationality) {
}
