package com.project.board.domain.image.controller;

import com.project.board.domain.image.dto.ImageUploadResponse;
import com.project.board.domain.image.service.ImageService;
import com.project.board.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<ImageUploadResponse>>> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<ImageUploadResponse> responses = files.stream()
                .map(file -> imageService.upload(file))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}