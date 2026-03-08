package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostLikeRepositoryCustom {
    Page<PostLike> findByUserIdWithPost(Long userId, Pageable pageable);

    Optional<PostLike> findByUserAndPost(Long userId, Long postId);

    boolean existsByUserAndPost(Long userId, Long postId);
}
