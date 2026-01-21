package com.project.board.domain.user.controller;

import com.project.board.domain.user.dto.LoginRequest;
import com.project.board.domain.user.dto.SignupRequest;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.service.UserService;
import com.project.board.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//restcontroller 기능이 뭐더라
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; //왜 파이널을 붙이더라

    @PostMapping("/signup")                                        //requestbody 어노테이션의 기능
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request){
        UserResponse response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request){
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인 성공"));
    }
}
