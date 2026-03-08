package com.project.board.domain.viewlog.repository;

public interface ViewLogRepositoryCustom {

    boolean existsByUserAndPost(Long userId, Long postId);
}
