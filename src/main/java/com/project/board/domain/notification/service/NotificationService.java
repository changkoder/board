package com.project.board.domain.notification.service;

import com.project.board.domain.notification.dto.NotificationResponse;
import com.project.board.domain.notification.entity.Notification;
import com.project.board.domain.notification.repository.NotificationRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void notify(User receiver, Notification.NotificationType type,
                       Long postId, Long commentId, Long actorId, String message){
        if (receiver.getId().equals(actorId)){
            return;
        }

        Notification notification = Notification.builder()
                .user(receiver)
                .type(type)
                .postId(postId)
                .commentId(commentId)
                .actorId(actorId)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(Long userId){
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notification -> NotificationResponse.from(notification))
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if(!notification.getUser().getId().equals(userId)){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notification.read();
    }

    @Transactional
    public void markAllAsRead(Long userId) { //여기는 왜 알림 아이디 필요없나
        notificationRepository.markAllAsRead(userId);
    }
}
