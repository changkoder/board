package com.project.board.domain.image.service;

import com.project.board.domain.image.dto.ImageUploadResponse;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-url}")
    private String accessUrl;

    public ImageUploadResponse upload(MultipartFile file) {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String extension = extractExtension(originalFileName);
        String savedFileName = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(savedFileName);
            file.transferTo(filePath.toFile());

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String imageUrl = accessUrl + "/" + savedFileName;
        return new ImageUploadResponse(imageUrl, originalFileName);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_IS_EMPTY);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        String extension = extractExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
