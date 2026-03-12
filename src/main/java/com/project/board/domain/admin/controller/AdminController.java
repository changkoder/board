package com.project.board.domain.admin.controller;

import com.project.board.domain.admin.service.AdminService;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/posts/hidden")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getHiddenPosts(Pageable pageable) {
        Page<PostListResponse> response = adminService.getHiddenPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/comments/hidden")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getHiddenComments(Pageable pageable) {
        Page<CommentResponse> response = adminService.getHiddenComments(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{postId}/hide")
    public ResponseEntity<ApiResponse<Void>> hidePost(@PathVariable Long postId) {
        adminService.hidePost(postId);
        return ResponseEntity.ok(ApiResponse.success(null, "게시글 숨김 처리 완료"));
    }

    @PatchMapping("/comments/{commentId}/hide")
    public ResponseEntity<ApiResponse<Void>> hideComment(@PathVariable Long commentId) {
        adminService.hideComment(commentId);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글 숨김 처리 완료"));
    }

    @PatchMapping("/posts/{postId}/restore")
    public ResponseEntity<ApiResponse<Void>> restorePost(@PathVariable Long postId) {
        adminService.restorePost(postId);
        return ResponseEntity.ok(ApiResponse.success(null, "게시글 숨김 해제 완료"));
    }

    @PatchMapping("/comments/{commentId}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreComment(@PathVariable Long commentId) {
        adminService.restoreComment(commentId);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글 숨김 해제 완료"));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.success(null, "게시글 강제 삭제 완료"));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글 강제 삭제 완료"));
    }

    @GetMapping("/users/blocked")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getBlockedUsers(Pageable pageable) {
        Page<UserResponse> response = adminService.getBlockedUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> blockUser(@PathVariable Long userId) {
        adminService.blockUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "회원 차단 완료"));
    }

    @DeleteMapping("/users/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable Long userId) {
        adminService.unblockUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "회원 차단 해제 완료"));
    }
}