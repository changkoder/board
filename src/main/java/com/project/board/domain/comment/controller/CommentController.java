package com.project.board.domain.comment.controller;

import com.project.board.domain.comment.dto.CommentCreateRequest;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.dto.CommentUpdateRequest;
import com.project.board.domain.comment.service.CommentService;
import com.project.board.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> findByPostId(
            @PathVariable Long postId
    ){
        List<CommentResponse> response = commentService.findByPostId(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.create(postId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CommentUpdateRequest request) {
        CommentResponse response = commentService.update(commentId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId) {
        commentService.delete(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
