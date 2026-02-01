package com.project.board.domain.post.repository;

import com.project.board.domain.post.dto.PostSearchCondition;
import com.project.board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    Page<Post> search(PostSearchCondition condition, Pageable pageable);

    List<Post> findPopularPosts(int days, int limit);

    List<Post> findAllNoOffset(Long lastPostId, int size);


}

