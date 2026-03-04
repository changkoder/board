package com.project.board.domain.comment.repository;

import com.project.board.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<Comment> findByUserIdActive(Long userId, Pageable pageable);

    Page<Comment> findHiddenComments(Pageable pageable);
}
