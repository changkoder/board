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

    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;
    private List<ReportSummaryResponse> reports;

    public static AdminCommentDetailResponse from(Comment comment, List<ReportSummaryResponse> reports) {
        String nickname = comment.getUser().isDeleted() ? "(탈퇴한 사용자)" : comment.getUser().getNickname();

        return AdminCommentDetailResponse.builder()
                .content(comment.getContent())
                .authorNickname(nickname)
                .createdAt(comment.getCreatedAt())
                .reports(reports)
                .build();
    }
}
