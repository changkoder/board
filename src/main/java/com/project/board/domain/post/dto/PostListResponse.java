package com.project.board.domain.post.dto;

import com.project.board.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long id;
    private String title;
    private String categoryName;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImg;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        String nickname = post.getUser().isDeleted() ? "(탈퇴한 사용자)" : post.getUser().getNickname();
        String profileImg = post.getUser().isDeleted() ? null : post.getUser().getProfileImg();

        return PostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .categoryName(post.getCategory().getName())
                .authorId(post.getUser().getId())
                .authorNickname(nickname)
                .authorProfileImg(profileImg)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}