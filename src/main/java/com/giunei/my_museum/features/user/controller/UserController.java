package com.giunei.my_museum.features.user.controller;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.core.service.CloudinaryService;
import com.giunei.my_museum.features.user.dto.UserReponse;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    @GetMapping("/{id}")
    public UserReponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping("/upload-profile-image")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file) {
        User user = SecurityUtils.getAuthenticatedUser();

        String imageUrl = cloudinaryService.upload(file);

        user.getProfile().setProfileImageUrl(imageUrl);
        userService.save(user);

        return imageUrl;
    }
}
