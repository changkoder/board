package com.project.board.global.security.jwt;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.project.board.global.security.jwt.QRefreshToken.refreshToken;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(refreshToken)
                        .where(refreshToken.userId.eq(userId))
                        .fetchOne()
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(refreshToken)
                        .where(refreshToken.token.eq(token))
                        .fetchOne()
        );
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory
                .delete(refreshToken)
                .where(refreshToken.userId.eq(userId))
                .execute();
    }

    @Override
    public long deleteExpiredTokens(LocalDateTime now) {
        return queryFactory
                .delete(refreshToken)
                .where(refreshToken.expiresAt.lt(now))
                .execute();
    }
}
