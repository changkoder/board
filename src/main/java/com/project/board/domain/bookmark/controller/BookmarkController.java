package com.project.board.domain.bookmark.controller;

import com.project.board.domain.bookmark.service.BookmarkService;
import com.project.board.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/posts/{postId}/bookmark")
    public ResponseEntity<ApiResponse<Boolean>> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        boolean bookmarked = bookmarkService.toggleBookmark(userId, postId);
        return ResponseEntity.ok(ApiResponse.success(bookmarked));
    }
}