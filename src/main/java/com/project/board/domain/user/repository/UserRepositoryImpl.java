package com.project.board.domain.user.repository;

import com.project.board.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.project.board.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .where(user.email.eq(email))
                        .fetchOne()
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return queryFactory
                .selectFrom(user)
                .where(user.email.eq(email))
                .fetchFirst() != null;
    }

    @Override
    public List<User> findByStatus(User.Status status) {
        return queryFactory
                .selectFrom(user)
                .where(user.status.eq(status))
                .fetch();
    }

    @Override
    public Page<User> findByStatus(User.Status status, Pageable pageable) {
        List<User> content = queryFactory
                .selectFrom(user)
                .where(user.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(user.status.eq(status));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .where(user.nickname.eq(nickname))
                        .fetchOne()
        );
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return queryFactory
                .selectFrom(user)
                .where(user.nickname.eq(nickname))
                .fetchFirst() != null;
    }
}
