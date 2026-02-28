package com.project.board.domain.user.dto;

import com.project.board.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private final Long id;
    private final String nickname;
    private final String profileImg;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImg = user.getProfileImg();
    }
}
