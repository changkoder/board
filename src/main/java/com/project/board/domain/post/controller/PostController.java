package com.project.board.domain.post.controller;

import com.project.board.domain.post.dto.*;
import com.project.board.domain.post.service.PostService;
import com.project.board.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> findAll(
    @RequestParam(required = false) Long categoryId,
    Pageable pageable
    ){
        Page<PostListResponse> response = postService.findAll(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/infinite")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> findAllNoOffset(
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "10") int size
    ){
        List<PostListResponse> response = postService.findAllNoOffset(lastPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> search(
            @ModelAttribute PostSearchCondition condition,
            Pageable pageable
    ) {
        Page<PostListResponse> response = postService.search(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostCreateRequest request
            ){
        PostResponse response = postService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> findById(
            @PathVariable Long postId,
           @AuthenticationPrincipal Long userId
    ){
        PostResponse response;

        if(userId != null){ //로그인 회원 조회 엔티티 추가, 비회원은 x
            response = postService.findById(postId, userId);
        } else {
            response = postService.findById(postId);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> update(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostUpdateRequest request) {
        PostResponse response = postService.update(postId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        postService.delete(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> findPopularPosts() {
        List<PostListResponse> response = postService.findPopularPosts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    }
