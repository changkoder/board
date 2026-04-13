package com.project.board.global.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    @DisplayName("Access Token 생성 - 정상적으로 토큰이 생성된다")
    void createAccessToken_success(){
        String token = jwtTokenProvider.createAccessToken(1L, "USER");
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("토큰에서 userId 추출 - 생성 시 넣은 userId가 그대로 나온다")
    void getUserId_success() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, "USER");

        // when
        Long userId = jwtTokenProvider.getUserId(token);

        // then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("토큰에서 role 추출 - 생성 시 넣은 role이 그대로 나온다")
    void getRole_success() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, "ADMIN");

        // when
        String role = jwtTokenProvider.getRole(token);

        // then
        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 유효한 토큰이면 true")
    void validateToken_valid() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, "USER");

        // when
        boolean result = jwtTokenProvider.validateToken(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 만료된 토큰이면 false")
    void validateToken_expired() {
        // given - 동일한 비밀키, validity만 0으로 설정하여 순수하게 만료만 테스트
        JwtTokenProvider expiredProvider = new JwtTokenProvider(jwtSecret, 0, 0);
        String token = expiredProvider.createAccessToken(1L, "USER");

        // when
        boolean result = jwtTokenProvider.validateToken(token);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 위조된 토큰이면 false")
    void validateToken_forged() {
        // given
        String differentSecret = Base64.getEncoder().encodeToString(
                "completely-different-secret-key-for-testing-12345678".getBytes()
        );
        JwtTokenProvider differentProvider = new JwtTokenProvider(differentSecret, 1800000, 604800000);
        String forgedToken = differentProvider.createAccessToken(1L, "USER");

        // when
        boolean result = jwtTokenProvider.validateToken(forgedToken);

        // then
        assertThat(result).isFalse();
    }
}