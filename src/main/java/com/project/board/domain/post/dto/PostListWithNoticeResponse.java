package com.project.board.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListWithNoticeResponse {
    private List<PostListResponse> notices;
    private Page<PostListResponse> posts;
}