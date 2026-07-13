package com.giunei.my_museum.profile.dto;

import com.giunei.my_museum.user.entity.Gender;
import com.giunei.my_museum.user.entity.Nationality;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CompleteProfileRequest(
        String name,
        Nationality nationality,
        Gender gender,
        LocalDate birthDate,
        String bio,

        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,

        @Email(message = "Email inválido")
        String email
) {
}
