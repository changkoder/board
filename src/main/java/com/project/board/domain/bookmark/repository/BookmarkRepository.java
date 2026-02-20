package com.project.board.domain.bookmark.repository;

import com.project.board.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.post.id = :postId")
    Optional<Bookmark> findByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT COUNT(b) > 0 FROM Bookmark b WHERE b.user.id = :userId AND b.post.id = :postId")
    boolean existsByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    // 마이페이지 - 북마크한 글
    @Query("SELECT b FROM Bookmark b JOIN FETCH b.post JOIN FETCH b.post.user JOIN FETCH b.post.category WHERE b.user.id = :userId AND b.post.deleted = false ORDER BY b.createdAt DESC")
    List<Bookmark> findByUserIdWithPost(@Param("userId") Long userId);

}