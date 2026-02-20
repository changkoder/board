package com.project.board.domain.report.dto;

import com.project.board.domain.report.entity.Report;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {

    @NotNull(message = "신고 사유를 선택해주세요.")
    private Report.ReportReason reason;
}