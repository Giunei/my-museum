package com.giunei.my_museum.features.user.preference.controller;

import com.giunei.my_museum.features.user.preference.dto.PreferenceRequest;
import com.giunei.my_museum.features.user.preference.dto.PreferenceResponse;
import com.giunei.my_museum.features.user.preference.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
