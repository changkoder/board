package com.project.board.domain.like.controller;

import com.project.board.domain.like.service.LikeService;
import com.project.board.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<Boolean>> togglePostLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ){
        boolean liked = likeService.togglePostLike(userId, postId);
        return ResponseEntity.ok(ApiResponse.success(liked));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleCommentLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId
    ){
        boolean liked = likeService.toggleCommentLike(userId, commentId);
        return ResponseEntity.ok(ApiResponse.success(liked));
    }
}
