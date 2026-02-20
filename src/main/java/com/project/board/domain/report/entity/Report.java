package com.project.board.domain.report.entity;

import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"}),      // 1번
        @UniqueConstraint(columnNames = {"user_id", "comment_id"})    // 2번
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 신고한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post; // 신고당한 게시글 (nullable)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; // 신고당한 댓글 (nullable)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Report(User user, Post post, Comment comment, ReportReason reason) {
        this.user = user;
        this.post = post;
        this.comment = comment;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public enum ReportReason {
        SPAM,           // 스팸/광고
        ABUSE,          // 욕설/비하
        INAPPROPRIATE,  // 부적절한 내용
        FALSE_INFO,     // 허위 정보
        OTHER           // 기타
    }
}