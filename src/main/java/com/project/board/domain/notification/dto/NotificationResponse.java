package com.project.board.domain.notification.dto;

import com.project.board.domain.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String type;
    private Long postId;
    private Long commentId;
    private Long actorId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .postId(notification.getPostId())
                .commentId(notification.getCommentId())
                .actorId(notification.getActorId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}