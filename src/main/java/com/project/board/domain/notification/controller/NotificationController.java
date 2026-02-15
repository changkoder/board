package com.project.board.domain.notification.controller;

import com.project.board.domain.notification.dto.NotificationResponse;
import com.project.board.domain.notification.service.NotificationService;
import com.project.board.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal Long userId) {
        List<NotificationResponse> response = notificationService.getMyNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "알림 읽음 처리 완료"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "전체 알림 읽음 처리 완료"));
    }
}