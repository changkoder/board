package com.project.board.domain.notification.repository;

import com.project.board.domain.notification.entity.Notification;

import java.util.List;

public interface NotificationRepositoryCustom {

    List<Notification> findByUserId(Long userId);

    long markAllAsRead(Long userId);

    boolean existsLikeNotification(Long receiverId, Long actorId, Long postId, Notification.NotificationType type);
}
