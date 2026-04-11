package com.project.board.domain.comment.dto;

import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.report.dto.ReportSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminCommentDetailResponse {

    private Long id;
    private String content;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImg;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hidden;
    private boolean deleted;
    private List<ReportSummaryResponse> reports;

    public static AdminCommentDetailResponse from(Comment comment, List<ReportSummaryResponse> reports) {
        String nickname = comment.getUser().isDeleted() ? "(탈퇴한 사용자)" : comment.getUser().getNickname();
        String profileImg = comment.getUser().isDeleted() ? null : comment.getUser().getProfileImg();

        return AdminCommentDetailResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getUser().getId())
                .authorNickname(nickname)
                .authorProfileImg(profileImg)
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .hidden(comment.isHidden())
                .deleted(comment.isDeleted())
                .reports(reports)
                .build();
    }
}
