package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.CommentLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.project.board.domain.like.entity.QCommentLike.commentLike;

@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CommentLike> findByUserAndComment(Long userId, Long commentId) {
        CommentLike result = queryFactory
                .selectFrom(commentLike)
                .where(
                        commentLike.user.id.eq(userId),
                        commentLike.comment.id.eq(commentId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByUserAndComment(Long userId, Long commentId) {
        Integer result = queryFactory
                .selectOne()
                .from(commentLike)
                .where(
                        commentLike.user.id.eq(userId),
                        commentLike.comment.id.eq(commentId)
                )
                .fetchFirst();

        return result != null;
    }
}
