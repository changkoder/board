package com.project.board.domain.viewlog.repository;

import com.project.board.domain.viewlog.entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {

    @Query("SELECT COUNT(vl) > 0 FROM ViewLog vl WHERE vl.user.id = :userId AND vl.post.id = :postId")
    boolean existsByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);
}
