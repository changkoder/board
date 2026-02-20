package com.project.board.domain.post.repository;

import com.project.board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.hidden = false")
    Page<Post> findAllActive(Pageable pageable);

    @Query("SELECT p from Post p WHERE p.category.id = :categoryId AND p.deleted = false AND  p.hidden = false")
    Page<Post> findByCategoryActive(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.category WHERE p.hidden = true AND p.deleted = false")
    List<Post> findByHiddenTrueAndDeletedFalse();

    // 마이페이지 - 내가 쓴 글
    @Query("SELECT p FROM Post p JOIN FETCH p.category WHERE p.user.id = :userId AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findByUserId(@Param("userId") Long userId);
}
