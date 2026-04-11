package com.project.board.domain.post.dto;

import com.project.board.domain.post.entity.Post;
import com.project.board.domain.report.dto.ReportSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminPostDetailResponse {

    private String title;
    private String content;
    private String categoryName;
    private String authorNickname;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private List<ReportSummaryResponse> reports;

    public static AdminPostDetailResponse from(Post post, List<ReportSummaryResponse> reports) {
        String nickname = post.getUser().isDeleted() ? "(탈퇴한 사용자)" : post.getUser().getNickname();

        return AdminPostDetailResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .authorNickname(nickname)
                .imageUrls(post.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .createdAt(post.getCreatedAt())
                .reports(reports)
                .build();
    }
}
