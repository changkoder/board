package com.project.board.domain.report.repository;

public interface ReportRepositoryCustom {

    boolean existsByUserAndPost(Long userId, Long postId);

    boolean existsByUserAndComment(Long userId, Long commentId);

    long countByPostId(Long postId);

    long countByCommentId(Long commentId);
}
