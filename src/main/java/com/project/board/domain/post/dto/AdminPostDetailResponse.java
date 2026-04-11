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

    private Long id;
    private String title;
    private String content;
    private String categoryName;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImg;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hidden;
    private boolean deleted;
    private List<ReportSummaryResponse> reports;

    public static AdminPostDetailResponse from(Post post, List<ReportSummaryResponse> reports) {
        String nickname = post.getUser().isDeleted() ? "(탈퇴한 사용자)" : post.getUser().getNickname();
        String profileImg = post.getUser().isDeleted() ? null : post.getUser().getProfileImg();

        return AdminPostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .authorId(post.getUser().getId())
                .authorNickname(nickname)
                .authorProfileImg(profileImg)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .imageUrls(post.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .hidden(post.isHidden())
                .deleted(post.isDeleted())
                .reports(reports)
                .build();
    }
}
