package com.giunei.my_museum.features.user.preference.service;

import com.giunei.my_museum.features.user.preference.dto.PreferenceResponse;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.preference.entity.PreferenceType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreferenceMapper {

    public PreferenceResponse toResponse(List<Preference> prefs) {
        return new PreferenceResponse(
                filter(prefs, PreferenceType.BOOK),
                filter(prefs, PreferenceType.MOVIE),
                filter(prefs, PreferenceType.SERIES),
                filter(prefs, PreferenceType.GAME)
        );
    }

    private List<String> filter(List<Preference> prefs, PreferenceType type) {
        return prefs.stream()
                .filter(p -> p.getType() == type)
                .map(Preference::getValue)
                .toList();
    }
}
