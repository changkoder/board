package com.project.board.domain.post.repository;

import com.project.board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.hidden = false")
    Page<Post> findAllActive(Pageable pageable);

    @Query("SELECT p from Post p WHERE p.category.id = :categoryId AND p.deleted = false AND  p.hidden = false")
    Page<Post> findByCategoryActive(@Param("categoryId") Long categoryId, Pageable pageable);
}
