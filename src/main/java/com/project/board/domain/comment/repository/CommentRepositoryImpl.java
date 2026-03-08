package com.project.board.domain.comment.repository;

import com.project.board.domain.comment.entity.Comment;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
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
                .join(comment.user).fetchJoin()
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

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        // 부모 댓글이면 자기 id, 대댓글이면 부모 id 기준으로 그룹핑 정렬
        NumberExpression<Long> groupId = new CaseBuilder()
                .when(comment.parent.isNull()).then(comment.id)
                .otherwise(comment.parent.id);

        return queryFactory
                .selectFrom(comment)
                .join(comment.user).fetchJoin()
                .where(
                        comment.post.id.eq(postId),
                        comment.deleted.eq(false),
                        comment.hidden.eq(false)
                )
                .orderBy(
                        groupId.asc(),
                        comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc()
                )
                .fetch();
    }
}
