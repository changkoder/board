package com.project.board.domain.post.repository;

import com.project.board.domain.post.dto.PostSearchCondition;
import com.project.board.domain.post.dto.SearchType;
import com.project.board.domain.post.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.board.domain.post.entity.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> search(PostSearchCondition condition, Pageable pageable) {

        List<Post> content = queryFactory
                .selectFrom(post)
                .where(post.deleted.eq(false),
                        post.hidden.eq(false),
                        categoryIdEq(condition.getCategoryId()),
                        searchKeyword(condition.getSearchType(), condition.getKeyword())
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory //카운트는 비정규화로 한거 아니였나 이건 일일이 카운트 쿼리를 날려서 세야하나
                .select(post.count())
                .from(post)
                .where(
                        post.deleted.eq(false),
                        post.hidden.eq(false),
                        categoryIdEq(condition.getCategoryId()),
                        searchKeyword(condition.getSearchType(), condition.getKeyword())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
        // 이 유틸 메서드 파라미터에 들어가는건 뭐지
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? post.category.id.eq(categoryId) : null; //이걸 굳이 메서드를 따로 뽑는이유?
    }

    private BooleanExpression searchKeyword(SearchType searchType, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return switch (searchType) {// 서치 타입을 이넘으로 만들면 좀 오버하는건가
            case TITLE -> post.title.contains(keyword);
            case CONTENT -> post.content.contains(keyword);
            case AUTHOR -> post.user.nickname.contains(keyword);
            case TITLE_CONTENT -> post.title.contains(keyword).or(post.content.contains(keyword));
        };
    }
}
