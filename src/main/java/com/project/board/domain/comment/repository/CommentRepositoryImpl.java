package com.project.board.domain.comment.repository;

import com.project.board.domain.comment.entity.Comment;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.board.domain.comment.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Comment> findByUserIdActive(Long userId, Pageable pageable) {
        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.post).fetchJoin()
                .where(
                        comment.user.id.eq(userId),
                        comment.deleted.eq(false),
                        comment.hidden.eq(false)
                )
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.user.id.eq(userId),
                        comment.deleted.eq(false),
                        comment.hidden.eq(false)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Comment> findHiddenComments(Pageable pageable) {
        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.user).fetchJoin()
                .join(comment.post).fetchJoin()
                .where(
                        comment.hidden.eq(true),
                        comment.deleted.eq(false)
                )
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.hidden.eq(true),
                        comment.deleted.eq(false)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
