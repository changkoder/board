package com.project.board.domain.notification.repository;

import com.project.board.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationRepositoryCustom {

    List<Notification> findByUserId(Long userId);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    long countUnreadByUserId(Long userId);

    long markAllAsRead(Long userId);

    boolean existsLikeNotification(Long receiverId, Long actorId, Long postId, Notification.NotificationType type);
}
