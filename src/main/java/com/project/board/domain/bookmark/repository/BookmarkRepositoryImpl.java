package com.project.board.domain.bookmark.repository;

import com.project.board.domain.bookmark.entity.Bookmark;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.board.domain.bookmark.entity.QBookmark.bookmark;

@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Bookmark> findByUserIdWithPost(Long userId, Pageable pageable) {
        List<Bookmark> content = queryFactory
                .selectFrom(bookmark)
                .join(bookmark.post).fetchJoin()
                .join(bookmark.post.user).fetchJoin()
                .join(bookmark.post.category).fetchJoin()
                .where(
                        bookmark.user.id.eq(userId),
                        bookmark.post.deleted.eq(false),
                        bookmark.post.hidden.eq(false)
                )
                .orderBy(bookmark.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(bookmark.count())
                .from(bookmark)
                .where(
                        bookmark.user.id.eq(userId),
                        bookmark.post.deleted.eq(false),
                        bookmark.post.hidden.eq(false)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
