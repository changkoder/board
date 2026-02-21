package com.project.board.domain.post.dto;

import com.project.board.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String categoryName;
    private Long authorId;
    private String authorNickname;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean bookmarked;

    public static PostResponse from(Post post){
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .authorId(post.getUser().getId())
                .authorNickname(post.getUser().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .imageUrls(post.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .bookmarked(false)
                .build();
    }

    public static PostResponse from(Post post, boolean bookmarked){
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .authorId(post.getUser().getId())
                .authorNickname(post.getUser().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .imageUrls(post.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .bookmarked(bookmarked)
                .build();
    }
}
