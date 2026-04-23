package com.giunei.my_museum.features.user.preference.controller;

import com.giunei.my_museum.features.user.preference.dto.PreferenceOption;
import com.giunei.my_museum.features.user.preference.dto.PreferenceOptionsResponse;
import com.giunei.my_museum.features.user.preference.dto.PreferenceRequest;
import com.giunei.my_museum.features.user.preference.dto.PreferenceResponse;
import com.giunei.my_museum.features.user.preference.entity.BookGenre;
import com.giunei.my_museum.features.user.preference.entity.GameGenre;
import com.giunei.my_museum.features.user.preference.entity.MovieGenre;
import com.giunei.my_museum.features.user.preference.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/preference")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService service;

    @PostMapping
    public void save(@RequestBody PreferenceRequest request) {
        service.savePreferences(request);
    }

    @PutMapping
    public void update(@RequestBody PreferenceRequest request) {
        service.updatePreferences(request);
    }

    @GetMapping("/me")
    public PreferenceResponse getMyPreferences() {
        return service.getMyPreferences();
    }

    @GetMapping("/options")
    public PreferenceOptionsResponse getOptions() {
        return new PreferenceOptionsResponse(
                mapBookGenres(),
                mapMovieGenres(),
                mapSeriesGenres(),
                mapGameGenres()
        );
    }

    private List<PreferenceOption> mapBookGenres() {
        return Arrays.stream(BookGenre.values())
                .map(g -> new PreferenceOption(g.name(), g.getLabel()))
                .toList();
    }

    private List<PreferenceOption> mapMovieGenres() {
        return Arrays.stream(MovieGenre.values())
                .map(g -> new PreferenceOption(g.name(), g.getLabel()))
                .toList();
    }

    private List<PreferenceOption> mapSeriesGenres() {
        return Arrays.stream(MovieGenre.values())
                .map(g -> new PreferenceOption(g.name(), g.getLabel()))
                .toList();
    }

    private List<PreferenceOption> mapGameGenres() {
        return Arrays.stream(GameGenre.values())
                .map(g -> new PreferenceOption(g.name(), g.getLabel()))
                .toList();
    }
}
