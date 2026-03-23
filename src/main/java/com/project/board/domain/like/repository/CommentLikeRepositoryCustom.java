package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.CommentLike;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentLikeRepositoryCustom {

    Optional<CommentLike> findByUserAndComment(Long userId, Long commentId);

    Set<Long> findLikedCommentIds(Long userId, List<Long> commentIds);
}
