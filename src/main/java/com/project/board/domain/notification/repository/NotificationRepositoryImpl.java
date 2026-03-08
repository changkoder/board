package com.project.board.domain.notification.repository;

import com.project.board.domain.notification.entity.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.project.board.domain.notification.entity.QNotification.notification;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notification> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(notification)
                .where(notification.user.id.eq(userId))
                .orderBy(notification.createdAt.desc())
                .fetch();
    }

    @Override
    public long markAllAsRead(Long userId) {
        return queryFactory
                .update(notification)
                .set(notification.isRead, true)
                .where(
                        notification.user.id.eq(userId),
                        notification.isRead.eq(false)
                )
                .execute();
    }

    @Override
    public boolean existsLikeNotification(Long receiverId, Long actorId, Long postId, Notification.NotificationType type) {
        Integer result = queryFactory
                .selectOne()
                .from(notification)
                .where(
                        notification.user.id.eq(receiverId),
                        notification.actorId.eq(actorId),
                        notification.postId.eq(postId),
                        notification.type.eq(type)
                )
                .fetchFirst();

        return result != null;
    }
}
