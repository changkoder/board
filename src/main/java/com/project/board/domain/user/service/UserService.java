package com.project.board.domain.user.service;

import com.project.board.domain.user.dto.LoginRequest;
import com.project.board.domain.user.dto.SignupRequest;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)//왜 읽기전용으로 해놓은거야?
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public UserResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);//패스워드 인코더가 뭐지? 그리고 디비에는 암호화된 상태의 비밀번호가 저장되나
        }

        return new UserResponse(user);
    }
}
