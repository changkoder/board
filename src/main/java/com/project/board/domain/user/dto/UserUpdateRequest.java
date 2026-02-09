package com.project.board.domain.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    @Size(min = 2, max = 20, message = "닉네임은 2~20자입니다")
    private String nickname;

    private String profileImg;
}
