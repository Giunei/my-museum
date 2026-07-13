package com.giunei.my_museum.common.storage;

import com.cloudinary.Cloudinary;
import com.giunei.my_museum.common.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final ProfileImageValidator profileImageValidator;

    public String upload(MultipartFile file) {
        profileImageValidator.validate(file);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "my-museum/profile-images",
                            "resource_type", "image",
                            "allowed_formats", List.of("jpg", "png", "webp")
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new FileUploadException("Resposta inválida do provedor de upload");
            }

            return secureUrl.toString();
        } catch (IOException e) {
            throw new FileUploadException("Erro ao fazer upload da imagem", e);
        }
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, Map.of());
            }
        } catch (IOException e) {
            throw new FileUploadException("Erro ao deletar imagem do Cloudinary", e);
        }
    }

    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            String filename = parts[parts.length - 1];
            return "my-museum/profile-images/" + filename.substring(0, filename.lastIndexOf('.'));
        } catch (Exception e) {
            return null;
        }
    }
}
