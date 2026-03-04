package com.project.board.domain.bookmark.repository;

import com.project.board.domain.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {
    Page<Bookmark> findByUserIdWithPost(Long userId, Pageable pageable);
}
