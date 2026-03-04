package com.project.board.domain.comment.dto;

import com.project.board.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImg;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> children;
    private Long postId;
    private String postTitle;

    public static CommentResponse from(Comment comment) {
        return from(comment, null);
    }

    public static CommentResponse from(Comment comment, List<CommentResponse> children) {
        String nickname = comment.getUser().isDeleted() ? "(탈퇴한 사용자)" : comment.getUser().getNickname();
        String profileImg = comment.getUser().isDeleted() ? null : comment.getUser().getProfileImg();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getUser().getId())
                .authorNickname(nickname)
                .authorProfileImg(profileImg)
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .postId(comment.getPost().getId())
                .postTitle(comment.getPost().getTitle())
                .children(children)
                .build();
    }
}
