package com.project.board.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListWithNoticeNoOffsetResponse {
    private List<PostListResponse> notices;
    private List<PostListResponse> posts;
}