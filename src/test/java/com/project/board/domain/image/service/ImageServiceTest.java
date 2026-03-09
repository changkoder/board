package com.project.board.domain.image.service;

import com.project.board.domain.image.dto.ImageUploadResponse;
import com.project.board.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Test
    @DisplayName("이미지 업로드 성공")
    void upload_success() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", new byte[]{1, 2, 3});

        // when
        ImageUploadResponse response = imageService.upload(file);

        // then
        assertThat(response.getImageUrl()).isNotNull();
    }

    @Test
    @DisplayName("빈 파일 업로드 시 예외")
    void upload_emptyFile() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]);

        // when & then
        assertThatThrownBy(() -> imageService.upload(file))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("이미지가 아닌 파일 업로드 시 예외")
    void upload_invalidFileType() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", new byte[]{1, 2, 3});

        // when & then
        assertThatThrownBy(() -> imageService.upload(file))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("10MB 초과 파일 업로드 시 예외")
    void upload_fileSizeExceeded() {
        // given
        byte[] largeContent = new byte[10 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.png", "image/png", largeContent);

        // when & then
        assertThatThrownBy(() -> imageService.upload(file))
                .isInstanceOf(CustomException.class);
    }
}
