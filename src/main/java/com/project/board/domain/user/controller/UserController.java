package com.project.board.domain.user.controller;

import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.user.dto.PasswordChangeRequest;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.dto.UserUpdateRequest;
import com.project.board.domain.user.service.UserService;
import com.project.board.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@AuthenticationPrincipal Long userId){
        UserResponse response = userService.getMyInfo(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateMyInfo(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "비밀번호 변경 성공"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@AuthenticationPrincipal Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴 성공"));
    }

    @GetMapping("/me/posts")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getMyPosts(
            @AuthenticationPrincipal Long userId) {
        List<PostListResponse> response = userService.getMyPosts(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getMyComments(
            @AuthenticationPrincipal Long userId) {
        List<CommentResponse> response = userService.getMyComments(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/likes")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getMyLikedPosts(
            @AuthenticationPrincipal Long userId) {
        List<PostListResponse> response = userService.getMyLikedPosts(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/bookmarks")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getMyBookmarkedPosts(
            @AuthenticationPrincipal Long userId) {
        List<PostListResponse> response = userService.getMyBookmarkedPosts(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
