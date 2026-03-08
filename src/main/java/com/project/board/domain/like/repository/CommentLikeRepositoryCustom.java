package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.CommentLike;

import java.util.Optional;

public interface CommentLikeRepositoryCustom {

    Optional<CommentLike> findByUserAndComment(Long userId, Long commentId);

    boolean existsByUserAndComment(Long userId, Long commentId);
}
