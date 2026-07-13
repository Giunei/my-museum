package com.giunei.my_museum.common.storage;

import com.giunei.my_museum.common.exception.InvalidFileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileImageValidatorTest {

    private ProfileImageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProfileImageValidator();
    }

    @Test
    void acceptsJpegPngAndWebp() {
        assertThatCode(() -> validator.validate(jpegFile("photo.jpg"))).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate(pngFile("photo.png"))).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate(webpFile("photo.webp"))).doesNotThrowAnyException();
    }

    @Test
    void rejectsGifByContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "anim.gif",
                "image/gif",
                gifBytes()
        );

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidFileUploadException.class)
                .hasMessageContaining("não permitido")
                .hasMessageContaining("GIF");
    }

    @Test
    void rejectsGifDisguisedAsJpeg() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fake.jpg",
                "image/jpeg",
                gifBytes()
        );

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidFileUploadException.class)
                .hasMessageContaining("não permitido");
    }

    @Test
    void rejectsNonImageContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.pdf",
                "application/pdf",
                jpegBytes()
        );

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidFileUploadException.class)
                .hasMessageContaining("não permitido");
    }

    @Test
    void rejectsEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidFileUploadException.class)
                .hasMessageContaining("vazio");
    }

    private static MockMultipartFile jpegFile(String name) {
        return new MockMultipartFile("file", name, "image/jpeg", jpegBytes());
    }

    private static MockMultipartFile pngFile(String name) {
        return new MockMultipartFile("file", name, "image/png", pngBytes());
    }

    private static MockMultipartFile webpFile(String name) {
        return new MockMultipartFile("file", name, "image/webp", webpBytes());
    }

    private static byte[] jpegBytes() {
        byte[] bytes = new byte[16];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xD8;
        bytes[2] = (byte) 0xFF;
        return bytes;
    }

    private static byte[] pngBytes() {
        return new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0, 0, 0, 0, 0, 0, 0, 0
        };
    }

    private static byte[] webpBytes() {
        return new byte[]{
                'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P', 0, 0, 0, 0
        };
    }

    private static byte[] gifBytes() {
        return new byte[]{'G', 'I', 'F', '8', '9', 'a', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }
}
