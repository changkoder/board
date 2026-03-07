package com.project.board.domain.post.repository;

import com.project.board.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{

    // 공지글 목록 (삭제/숨김 제외)
    @Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.category WHERE p.category.name = '공지' AND p.deleted = false AND p.hidden = false ORDER BY p.createdAt DESC")
    List<Post> findNotices();

    @Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.category LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findByIdWithDetails(@Param("id") Long id);
}
