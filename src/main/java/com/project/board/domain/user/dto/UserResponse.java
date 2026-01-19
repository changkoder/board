package com.project.board.domain.user.dto;

import com.project.board.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImg;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImg = user.getProfileImg();
    }
}
