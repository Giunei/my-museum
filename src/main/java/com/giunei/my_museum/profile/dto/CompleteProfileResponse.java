package com.giunei.my_museum.profile.dto;

import com.giunei.my_museum.user.entity.Gender;
import com.giunei.my_museum.user.entity.Nationality;
import com.giunei.my_museum.user.entity.User;

public record CompleteProfileResponse(
        String name,
        Nationality nationality,
        Gender gender
) {
    public static CompleteProfileResponse from(User user) {
        return new CompleteProfileResponse(
                user.getPerson().getName(),
                user.getPerson().getNationality(),
                user.getPerson().getGender()
        );
    }
}
