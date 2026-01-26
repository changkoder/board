package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.PostLike;
import com.project.board.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT pl FROM PostLike pl " +
            "WHERE pl.user.id = :userId " +
            "AND pl.post.id = :postId")
    Optional<PostLike> findByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT COUNT(pl) > 0 " +
            "FROM PostLike pl " +
            "WHERE pl.user.id = :userId " +
            "AND pl.post.id = :postId")
    boolean existsByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);
}
