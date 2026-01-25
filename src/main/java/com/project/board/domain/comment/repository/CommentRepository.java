package com.project.board.domain.comment.repository;

import com.project.board.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "AND c.deleted = false " +
            "AND c.hidden = false " +
            "ORDER BY c.createdAt ASC") //사실 해당 유저의 모든 정보가 필요하진 않지 않나
    List<Comment> findParentsByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.parent.id = :parentId " +
            "AND c.deleted = false " +
            "AND c.hidden = false " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findChildrenByParentId(@Param("parentId") Long parentId);
}
