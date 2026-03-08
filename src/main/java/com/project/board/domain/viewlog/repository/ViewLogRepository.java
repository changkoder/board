package com.project.board.domain.viewlog.repository;

import com.project.board.domain.viewlog.entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewLogRepository extends JpaRepository<ViewLog, Long>, ViewLogRepositoryCustom {

}
