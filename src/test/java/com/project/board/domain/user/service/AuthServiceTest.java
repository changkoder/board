package com.project.board.domain.user.service;

import com.project.board.domain.user.dto.*;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequest request = createSignupRequest("test@test.com", "password123", "tester");

        // when
        UserResponse response = authService.signup(request);

        // then
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_duplicateEmail() {
        // given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester1"));
        SignupRequest duplicate = createSignupRequest("test@test.com", "password456", "tester2");

        // when & then
        assertThatThrownBy(() -> authService.signup(duplicate))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("로그인 성공 - 토큰이 반환된다")
    void login_success(){
        //given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester"));
        LoginRequest loginRequest = createLoginRequest("test@test.com", "password123");

        // when
        TokenResponse response = authService.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void login_wrongPassword() {
        // given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester"));
        LoginRequest loginRequest = createLoginRequest("test@test.com", "wrongPassword");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(CustomException.class);
    }


    @Test
    @DisplayName("토큰 재발급 성공 - 유효한 Refresh Token으로 새 Access/Refresh Token 발급")
    void refresh_success() throws InterruptedException {
        // given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester"));
        TokenResponse loginResponse = authService.login(createLoginRequest("test@test.com", "password123"));

        RefreshRequest refreshRequest = new RefreshRequest();
        ReflectionTestUtils.setField(refreshRequest, "refreshToken", loginResponse.getRefreshToken());
        Thread.sleep(1000);

        // when
        TokenResponse response = authService.refresh(refreshRequest);

        // then
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getAccessToken()).isNotEqualTo(loginResponse.getAccessToken());
        assertThat(response.getRefreshToken()).isNotEqualTo(loginResponse.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_notFoundEmail() {
        // given
        LoginRequest loginRequest = createLoginRequest("notfound@test.com", "password123");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 차단된 유저")
    void login_blockedUser() {
        // given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester"));
        User user = userRepository.findByEmail("test@test.com").orElseThrow();
        user.block();

        LoginRequest loginRequest = createLoginRequest("test@test.com", "password123");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 탈퇴한 유저")
    void login_deletedUser() {
        // given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester"));
        User user = userRepository.findByEmail("test@test.com").orElseThrow();
        user.delete();

        LoginRequest loginRequest = createLoginRequest("test@test.com", "password123");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() {
        // given
        authService.signup(createSignupRequest("test@test.com", "password123", "tester"));
        TokenResponse loginResponse = authService.login(createLoginRequest("test@test.com", "password123"));
        User user = userRepository.findByEmail("test@test.com").orElseThrow();

        // when & then
        assertDoesNotThrow(() -> authService.logout(user.getId()));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 Refresh Token")
    void refresh_invalidToken() {
        // given
        RefreshRequest refreshRequest = new RefreshRequest();
        ReflectionTestUtils.setField(refreshRequest, "refreshToken", "invalid-token");

        // when & then
        assertThatThrownBy(() -> authService.refresh(refreshRequest))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signup_duplicateNickname() {
        // given
        authService.signup(createSignupRequest("test1@test.com", "password123", "sameNick"));
        SignupRequest duplicate = createSignupRequest("test2@test.com", "password456", "sameNick");

        // when & then
        assertThatThrownBy(() -> authService.signup(duplicate))
                .isInstanceOf(CustomException.class);
    }

    private SignupRequest createSignupRequest(String email, String password, String nickname) {
        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        ReflectionTestUtils.setField(request, "nickname", nickname);
        return request;
    }

    private LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

}