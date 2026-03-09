package com.project.board.domain.notification.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.notification.dto.NotificationResponse;
import com.project.board.domain.notification.entity.Notification;
import com.project.board.domain.notification.repository.NotificationRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    private User receiver;
    private User actor;
    private Post post;

    @BeforeEach
    void setUp() {
        receiver = userRepository.save(User.builder()
                .email("receiver@test.com")
                .password("password")
                .nickname("receiver")
                .build());

        actor = userRepository.save(User.builder()
                .email("actor@test.com")
                .password("password")
                .nickname("actor")
                .build());

        Category category = categoryRepository.save(new Category("자유"));

        post = postRepository.save(Post.builder()
                .user(actor)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build());
    }

    @Test
    @DisplayName("알림 생성 성공")
    void notify_success() {
        // when
        notificationService.notify(receiver, Notification.NotificationType.COMMENT,
                post.getId(), null, actor.getId(), "댓글 알림");

        // then
        List<NotificationResponse> notifications = notificationService.getMyNotifications(receiver.getId());
        assertThat(notifications).hasSize(1);
    }

    @Test
    @DisplayName("내 알림 목록 조회")
    void getMyNotifications_success() {
        // given
        notificationService.notify(receiver, Notification.NotificationType.COMMENT,
                post.getId(), null, actor.getId(), "알림1");
        notificationService.notify(receiver, Notification.NotificationType.REPLY,
                post.getId(), null, actor.getId(), "알림2");

        // when
        List<NotificationResponse> result = notificationService.getMyNotifications(receiver.getId());

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void markAsRead_success() {
        // given
        notificationService.notify(receiver, Notification.NotificationType.COMMENT,
                post.getId(), null, actor.getId(), "알림");
        List<NotificationResponse> notifications = notificationService.getMyNotifications(receiver.getId());
        Long notificationId = notifications.get(0).getId();

        // when
        notificationService.markAsRead(notificationId, receiver.getId());

        // then
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    @DisplayName("알림 전체 읽음 처리")
    void markAllAsRead_success() {
        // given
        notificationService.notify(receiver, Notification.NotificationType.COMMENT,
                post.getId(), null, actor.getId(), "알림1");
        notificationService.notify(receiver, Notification.NotificationType.REPLY,
                post.getId(), null, actor.getId(), "알림2");
        notificationService.notify(receiver, Notification.NotificationType.MENTION,
                post.getId(), null, actor.getId(), "알림3");

        // when
        notificationService.markAllAsRead(receiver.getId());
        em.flush();
        em.clear();

        // then
        List<Notification> all = notificationRepository.findByUserId(receiver.getId());
        assertThat(all).allMatch(Notification::isRead);
    }

    @Test
    @DisplayName("본인에게는 알림 안 감")
    void notify_selfNotification_skipped() {
        // when
        notificationService.notify(actor, Notification.NotificationType.COMMENT,
                post.getId(), null, actor.getId(), "본인 알림");

        // then
        List<NotificationResponse> notifications = notificationService.getMyNotifications(actor.getId());
        assertThat(notifications).isEmpty();
    }

    @Test
    @DisplayName("좋아요 중복 알림 방지")
    void notify_duplicateLike_prevented() {
        // given
        notificationService.notify(receiver, Notification.NotificationType.POST_LIKE,
                post.getId(), null, actor.getId(), "좋아요 알림");

        // when
        notificationService.notify(receiver, Notification.NotificationType.POST_LIKE,
                post.getId(), null, actor.getId(), "좋아요 알림");

        // then
        List<NotificationResponse> notifications = notificationService.getMyNotifications(receiver.getId());
        assertThat(notifications).hasSize(1);
    }

    @Test
    @DisplayName("타인 알림 읽음 처리 시 예외")
    void markAsRead_otherUser_throwsException() {
        // given
        notificationService.notify(receiver, Notification.NotificationType.COMMENT,
                post.getId(), null, actor.getId(), "알림");
        List<NotificationResponse> notifications = notificationService.getMyNotifications(receiver.getId());
        Long notificationId = notifications.get(0).getId();

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(notificationId, actor.getId()))
                .isInstanceOf(CustomException.class);
    }
}
