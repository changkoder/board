package com.project.board.global.security.jwt;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.project.board.global.security.jwt.QRefreshToken.refreshToken;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteExpiredTokens(LocalDateTime now) {
        return queryFactory
                .delete(refreshToken)
                .where(refreshToken.expiresAt.lt(now))
                .execute();
    }
}
