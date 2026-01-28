package com.project.board.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchCondition {

    private SearchType searchType;
    private String keyword;
    private Long categoryId;
}
