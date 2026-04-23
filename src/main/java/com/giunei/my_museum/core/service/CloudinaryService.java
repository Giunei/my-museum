package com.giunei.my_museum.core.service;

import com.cloudinary.Cloudinary;
import com.giunei.my_museum.exceptions.FileUploadException;
import com.giunei.my_museum.exceptions.InvalidFileUploadException;
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
