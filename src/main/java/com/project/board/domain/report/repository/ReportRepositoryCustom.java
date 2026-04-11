package com.project.board.domain.report.repository;

import com.project.board.domain.report.entity.Report;

import java.util.List;

public interface ReportRepositoryCustom {

    boolean existsByUserAndPost(Long userId, Long postId);

    boolean existsByUserAndComment(Long userId, Long commentId);

    long countByPostId(Long postId);

    long countByCommentId(Long commentId);

    List<Report> findAllByPostIdWithReporter(Long postId);

    List<Report> findAllByCommentIdWithReporter(Long commentId);
}
