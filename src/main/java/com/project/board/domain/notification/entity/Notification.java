package com.project.board.domain.notification.entity;

import com.project.board.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림 받는 사람

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private Long postId;

    private Long commentId;

    @Column(nullable = false)
    private Long actorId; // 알림 발생시킨 사람

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(User user, NotificationType type, Long postId,
                        Long commentId, Long actorId, String message) {
        this.user = user;
        this.type = type;
        this.postId = postId;
        this.commentId = commentId;
        this.actorId = actorId;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public void read() {
        this.isRead = true;
    }

    public enum NotificationType {
        COMMENT,       // 내 글에 댓글
        REPLY,         // 내 댓글에 대댓글
        POST_LIKE,     // 내 글에 좋아요
        COMMENT_LIKE   // 내 댓글에 좋아요
    }
}
