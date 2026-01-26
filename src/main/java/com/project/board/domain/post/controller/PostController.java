package com.project.board.domain.post.controller;

import com.project.board.domain.post.dto.PostCreateRequest;
import com.project.board.domain.post.dto.PostResponse;
import com.project.board.domain.post.dto.PostUpdateRequest;
import com.project.board.domain.post.service.PostService;
import com.project.board.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> findAll(
    @RequestParam(required = false) Long categoryId,
    Pageable pageable
    ){
        Page<PostResponse> response = postService.findAll(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @RequestParam Long userId, //나중에 jwt에서 추출
            @Valid @RequestBody PostCreateRequest request
            ){
        PostResponse response = postService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> findById(
            @PathVariable Long postId,
            @RequestParam(required = false) Long userId
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
            @Valid @RequestBody PostUpdateRequest request) {
        PostResponse response = postService.update(postId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
