package com.project.board.domain.notification.repository;

import com.project.board.domain.notification.entity.Notification;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.board.domain.notification.entity.QNotification.notification;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notification> findByUserId(Long userId, Pageable pageable) {
        List<Notification> content = queryFactory
                .selectFrom(notification)
                .where(notification.user.id.eq(userId))
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notification.count())
                .from(notification)
                .where(notification.user.id.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        Long count = queryFactory
                .select(notification.count())
                .from(notification)
                .where(
                        notification.user.id.eq(userId),
                        notification.isRead.eq(false)
                )
                .fetchOne();
        return count != null ? count : 0L;
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
