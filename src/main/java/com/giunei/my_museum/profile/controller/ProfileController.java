package com.giunei.my_museum.profile.controller;

import com.giunei.my_museum.profile.dto.CompleteProfileRequest;
import com.giunei.my_museum.profile.dto.CompleteProfileResponse;
import com.giunei.my_museum.profile.dto.ProfileResponse;
import com.giunei.my_museum.profile.dto.UpdateProfilePrivacyRequest;
import com.giunei.my_museum.profile.dto.UpdateProfileThemeRequest;
import com.giunei.my_museum.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping("/me")
    public ProfileResponse getMyProfile() {
        return service.getMyProfile();
    }

    @PatchMapping
    public CompleteProfileResponse completeProfile(@RequestBody CompleteProfileRequest request) {
        return service.completeProfile(request);
    }

    @PatchMapping("/theme")
    public ResponseEntity<Void> updateTheme(@RequestBody UpdateProfileThemeRequest request) {
        service.updateTheme(request.theme());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/privacy")
    public ResponseEntity<Void> updatePrivacy(@RequestBody @Valid UpdateProfilePrivacyRequest request) {
        service.updatePrivacy(request.privateProfile());
        return ResponseEntity.noContent().build();
    }
}
