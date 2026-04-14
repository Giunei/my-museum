package com.giunei.my_museum.core.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        validateImage(file);
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "my-museum/profile-images")
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem");
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Arquivo deve ser uma imagem");
        }

        if (file.getSize() > 2 * 1024 * 1024) { // 2MB
            throw new RuntimeException("Imagem muito grande (máx 2MB)");
        }
    }
}
