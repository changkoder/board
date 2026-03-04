package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.PostLike;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.board.domain.like.entity.QPostLike.postLike;

@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostLike> findByUserIdWithPost(Long userId, Pageable pageable) {
        List<PostLike> content = queryFactory
                .selectFrom(postLike)
                .join(postLike.post).fetchJoin()
                .join(postLike.post.user).fetchJoin()
                .join(postLike.post.category).fetchJoin()
                .where(
                        postLike.user.id.eq(userId),
                        postLike.post.deleted.eq(false),
                        postLike.post.hidden.eq(false)
                )
                .orderBy(postLike.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(postLike.count())
                .from(postLike)
                .where(
                        postLike.user.id.eq(userId),
                        postLike.post.deleted.eq(false),
                        postLike.post.hidden.eq(false)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
