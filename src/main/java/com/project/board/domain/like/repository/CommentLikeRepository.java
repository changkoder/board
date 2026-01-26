package com.project.board.domain.like.repository;

import com.project.board.domain.like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT cl FROM CommentLike cl " +
            "WHERE cl.user.id = :userId " +
            "AND cl.comment.id = :commentId")
    Optional<CommentLike> findByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Query("SELECT COUNT(cl) > 0 " +
            "FROM CommentLike cl " +
            "WHERE cl.user.id = :userId " +
            "AND cl.comment.id = :commentId")
    boolean existsByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
