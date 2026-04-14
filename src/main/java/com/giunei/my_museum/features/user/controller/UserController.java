package com.giunei.my_museum.features.user.controller;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.core.service.CloudinaryService;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @PostMapping("/upload-profile-image")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file) {
        User user = SecurityUtils.getAuthenticatedUser();

        String imageUrl = cloudinaryService.upload(file);

        user.getProfile().setProfileImageUrl(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }
}
