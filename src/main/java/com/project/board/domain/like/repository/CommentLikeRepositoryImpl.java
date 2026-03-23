package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.CommentLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Set<Long> findLikedCommentIds(Long userId, List<Long> commentIds) {
        List<Long> ids = queryFactory
                .select(commentLike.comment.id)
                .from(commentLike)
                .where(
                        commentLike.user.id.eq(userId),
                        commentLike.comment.id.in(commentIds)
                )
                .fetch();

        return new HashSet<>(ids);
    }
}
