package com.project.board.global.security.jwt;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepositoryCustom {

    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
    long deleteExpiredTokens(LocalDateTime now);
}
