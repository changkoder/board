package com.project.board.domain.report.dto;

import com.project.board.domain.report.entity.Report;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportSummaryResponse {

    private Long id;
    private Long reporterId;
    private String reporterNickname;
    private Report.ReportReason reason;
    private String reasonLabel;
    private LocalDateTime createdAt;

    public static ReportSummaryResponse from(Report report) {
        String nickname = report.getUser().isDeleted() ? "(탈퇴한 사용자)" : report.getUser().getNickname();

        return ReportSummaryResponse.builder()
                .id(report.getId())
                .reporterId(report.getUser().getId())
                .reporterNickname(nickname)
                .reason(report.getReason())
                .reasonLabel(toLabel(report.getReason()))
                .createdAt(report.getCreatedAt())
                .build();
    }

    private static String toLabel(Report.ReportReason reason) {
        return switch (reason) {
            case SPAM -> "스팸/광고";
            case ABUSE -> "욕설/비하";
            case INAPPROPRIATE -> "부적절한 내용";
            case FALSE_INFO -> "허위 정보";
            case OTHER -> "기타";
        };
    }
}
