package com.giunei.my_museum.common.storage;

import com.giunei.my_museum.common.exception.InvalidFileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

@Component
public class ProfileImageValidator {

    static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024;

    private static final String NOT_ALLOWED_MESSAGE =
            "Arquivo não permitido. Envie uma imagem JPG, PNG ou WebP (GIF não é aceito).";

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileUploadException("Arquivo vazio");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileUploadException("Imagem muito grande (máx 2MB)");
        }

        String contentType = normalizeContentType(file.getContentType());
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileUploadException(NOT_ALLOWED_MESSAGE);
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (extension != null && !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileUploadException(NOT_ALLOWED_MESSAGE);
        }

        byte[] header = readHeader(file);
        if (!isAllowedImageMagic(header)) {
            throw new InvalidFileUploadException(NOT_ALLOWED_MESSAGE);
        }
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }
        int semicolon = contentType.indexOf(';');
        String base = semicolon >= 0 ? contentType.substring(0, semicolon) : contentType;
        return base.trim().toLowerCase(Locale.ROOT);
    }

    private static String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static byte[] readHeader(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length < 12) {
                throw new InvalidFileUploadException(NOT_ALLOWED_MESSAGE);
            }
            return bytes;
        } catch (IOException e) {
            throw new InvalidFileUploadException("Não foi possível ler o arquivo enviado");
        }
    }

    private static boolean isAllowedImageMagic(byte[] bytes) {
        if (isGif(bytes)) {
            return false;
        }
        return isJpeg(bytes) || isPng(bytes) || isWebp(bytes);
    }

    private static boolean isGif(byte[] bytes) {
        return bytes.length >= 6
                && bytes[0] == 'G'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == '8'
                && (bytes[4] == '7' || bytes[4] == '9')
                && bytes[5] == 'a';
    }

    private static boolean isJpeg(byte[] bytes) {
        return bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF;
    }

    private static boolean isPng(byte[] bytes) {
        return bytes[0] == (byte) 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A;
    }

    private static boolean isWebp(byte[] bytes) {
        return bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P';
    }
}
