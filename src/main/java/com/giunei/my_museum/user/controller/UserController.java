package com.giunei.my_museum.user.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.common.storage.CloudinaryService;
import com.giunei.my_museum.user.dto.UserResponse;
import com.giunei.my_museum.user.dto.UserSearchResponse;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.profile.dto.ProfileResponse;
import com.giunei.my_museum.profile.service.ProfileService;
import com.giunei.my_museum.user.service.UserService;
import com.giunei.my_museum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfileService profileService;

    @GetMapping("/username/{username}/profile")
    public ProfileResponse profileByUsername(@PathVariable String username) {
        return profileService.getProfileByUsername(username);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/search")
    public List<UserSearchResponse> searchByUsername(@RequestParam String query) {
        return userRepository.searchByUsername(query)
                .stream()
                .map(user -> new UserSearchResponse(user.getId(), user.getUsername()))
                .toList();
    }

    @PostMapping("/upload-profile-image")
    @Transactional
    public String uploadProfileImage(@RequestParam("file") MultipartFile file) {
        User user = SecurityUtils.getAuthenticatedUser();

        String oldImageUrl = user.getProfile().getProfileImageUrl();
        String newImageUrl = cloudinaryService.upload(file);

        user.getProfile().setProfileImageUrl(newImageUrl);
        userService.save(user);

        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            cloudinaryService.delete(oldImageUrl);
        }

        return newImageUrl;
    }

    @DeleteMapping("/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void clearProfileImage() {
        User user = SecurityUtils.getAuthenticatedUser();

        String oldImageUrl = user.getProfile().getProfileImageUrl();
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            cloudinaryService.delete(oldImageUrl);
        }

        user.getProfile().setProfileImageUrl(null);
        userService.save(user);
    }
}
