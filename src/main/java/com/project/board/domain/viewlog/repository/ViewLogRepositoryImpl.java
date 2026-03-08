package com.project.board.domain.viewlog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.project.board.domain.viewlog.entity.QViewLog.viewLog;

@RequiredArgsConstructor
public class ViewLogRepositoryImpl implements ViewLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserAndPost(Long userId, Long postId) {
        Integer result = queryFactory
                .selectOne()
                .from(viewLog)
                .where(
                        viewLog.user.id.eq(userId),
                        viewLog.post.id.eq(postId)
                )
                .fetchFirst();

        return result != null;
    }
}
