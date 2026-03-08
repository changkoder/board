package com.project.board.domain.post.repository;

import com.project.board.domain.post.dto.PostSearchCondition;
import com.project.board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    Page<Post> search(PostSearchCondition condition, Pageable pageable);

    List<Post> findPopularPosts(int days, int limit);

    List<Post> findAllNoOffset(Long lastPostId, int size);

    Page<Post> findAllActive(Pageable pageable);

    Page<Post> findByCategoryActive(Long categoryId, Pageable pageable);

    Page<Post> findByUserIdActive(Long userId, Pageable pageable);

    Page<Post> findHiddenPosts(Pageable pageable);

    List<Post> findNotices();

    Optional<Post> findByIdWithDetails(Long id);
}

