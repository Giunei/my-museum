package com.giunei.my_museum.features.user.preference.service;

import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.preference.dto.PreferenceRequest;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.preference.entity.PreferenceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PreferenceBuilder {

    public List<Preference> build(User user, PreferenceRequest request) {
        List<Preference> list = new ArrayList<>();

        add(list, user, PreferenceType.BOOK, request.bookGenres());
        add(list, user, PreferenceType.MOVIE, request.movieGenres());
        add(list, user, PreferenceType.SERIES, request.seriesGenres());
        add(list, user, PreferenceType.GAME, request.gameGenres());

        return list;
    }

    private void add(List<Preference> list, User user, PreferenceType type, List<?> values) {
        if (values == null || values.isEmpty()) return;

        values.stream()
                .distinct()
                .forEach(v -> list.add(
                        Preference.builder()
                                .user(user)
                                .type(type)
                                .value(((Enum<?>) v).name())
                                .build()
                ));
    }
}
