package com.project.board.domain.notification.service;

import com.project.board.domain.notification.dto.NotificationResponse;
import com.project.board.domain.notification.entity.Notification;
import com.project.board.domain.notification.repository.NotificationRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.board.domain.notification.entity.Notification.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notify(User receiver, NotificationType type,
                       Long postId, Long commentId, Long actorId,
                       String actorNickname, String actorProfileImg, String message){
        if (receiver.getId().equals(actorId)){
            return;
        }

        //알림 중복 방지
        if (type == NotificationType.POST_LIKE || type == NotificationType.COMMENT_LIKE) {
            if (notificationRepository.existsLikeNotification(receiver.getId(), actorId, postId, commentId, type)) {
                return;
            }
        }

        Notification notification = builder()
                .user(receiver)
                .type(type)
                .postId(postId)
                .commentId(commentId)
                .actorId(actorId)
                .actorNickname(actorNickname)
                .actorProfileImg(actorProfileImg)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(NotificationResponse::from);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
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
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
