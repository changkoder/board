package com.project.board.domain.comment.repository;

import com.project.board.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "AND c.deleted = false " +
            "AND c.hidden = false " +
            "ORDER BY " +
            "CASE WHEN c.parent IS NULL THEN c.id ELSE c.parent.id END ASC, " +
            "c.parent.id ASC NULLS FIRST, " +
            "c.createdAt ASC")
    List<Comment> findAllByPostId(@Param("postId") Long postId);
}
