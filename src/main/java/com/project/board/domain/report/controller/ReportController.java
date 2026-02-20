package com.project.board.domain.report.controller;

import com.project.board.domain.report.dto.ReportRequest;
import com.project.board.domain.report.service.ReportService;
import com.project.board.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/api/posts/{postId}/report")
    public ResponseEntity<ApiResponse<Void>> reportPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReportRequest request) {
        reportService.reportPost(userId, postId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "게시글 신고 완료"));
    }

    @PostMapping("/api/comments/{commentId}/report")
    public ResponseEntity<ApiResponse<Void>> reportComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReportRequest request) {
        reportService.reportComment(userId, commentId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글 신고 완료"));
    }
}