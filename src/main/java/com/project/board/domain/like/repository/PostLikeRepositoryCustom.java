package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLikeRepositoryCustom {
    Page<PostLike> findByUserIdWithPost(Long userId, Pageable pageable);
}
