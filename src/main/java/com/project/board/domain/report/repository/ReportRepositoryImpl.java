package com.project.board.domain.report.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.project.board.domain.report.entity.QReport.report;

@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserAndPost(Long userId, Long postId) {
        Integer result = queryFactory
                .selectOne()
                .from(report)
                .where(
                        report.user.id.eq(userId),
                        report.post.id.eq(postId)
                )
                .fetchFirst();

        return result != null;
    }

    @Override
    public boolean existsByUserAndComment(Long userId, Long commentId) {
        Integer result = queryFactory
                .selectOne()
                .from(report)
                .where(
                        report.user.id.eq(userId),
                        report.comment.id.eq(commentId)
                )
                .fetchFirst();

        return result != null;
    }

    @Override
    public long countByPostId(Long postId) {
        return queryFactory
                .select(report.count())
                .from(report)
                .where(report.post.id.eq(postId))
                .fetchOne();
    }

    @Override
    public long countByCommentId(Long commentId) {
        return queryFactory
                .select(report.count())
                .from(report)
                .where(report.comment.id.eq(commentId))
                .fetchOne();
    }
}
