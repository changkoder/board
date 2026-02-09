package com.project.board.domain.user.service;

import com.project.board.domain.user.dto.*;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import com.project.board.global.security.jwt.JwtTokenProvider;
import com.project.board.global.security.jwt.RefreshToken;
import com.project.board.global.security.jwt.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)//왜 읽기전용으로 해놓은거야?
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Transactional
    public UserResponse signup(SignupRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

    public TokenResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);//패스워드 인코더가 뭐지? 그리고 디비에는 암호화된 상태의 비밀번호가 저장되나
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getRole().name());

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenValidity / 1000);

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken, expiresAt))
                );
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout(Long userId){
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request){
        if(!jwtTokenProvider.validateToken(request.getRefreshToken())){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));//위에서 유효성 검증을 했는데, 디비에 없을수도 있나

        Long userId = jwtTokenProvider.getUserId(request.getRefreshToken());
        String role = jwtTokenProvider.getRole(request.getRefreshToken());

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, role);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenValidity / 1000);
        savedToken.updateToken(newRefreshToken, expiresAt);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }
}
