package com.giunei.my_museum.common.storage;

import com.cloudinary.Cloudinary;
import com.giunei.my_museum.common.exception.FileUploadException;
import com.giunei.my_museum.common.exception.InvalidFileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024;

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        validateImage(file);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "my-museum/profile-images")
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

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileUploadException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileUploadException("Arquivo deve ser uma imagem");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileUploadException("Imagem muito grande (máx 2MB)");
        }
    }
}
