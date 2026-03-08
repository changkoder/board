package com.project.board.global.security.jwt;

import java.time.LocalDateTime;

public interface RefreshTokenRepositoryCustom {

    long deleteExpiredTokens(LocalDateTime now);
}
