package com.project.board.domain.report.repository;

import com.project.board.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 게시글 중복 신고 방지
    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.user.id = :userId AND r.post.id = :postId")
    boolean existsByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    // 댓글 중복 신고 방지
    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.user.id = :userId AND r.comment.id = :commentId")
    boolean existsByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    // 게시글 신고 수 카운트
    @Query("SELECT COUNT(r) FROM Report r WHERE r.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    // 댓글 신고 수 카운트
    @Query("SELECT COUNT(r) FROM Report r WHERE r.comment.id = :commentId")
    long countByCommentId(@Param("commentId") Long commentId);
}
