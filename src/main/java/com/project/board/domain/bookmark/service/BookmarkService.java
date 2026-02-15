package com.project.board.domain.bookmark.service;

import com.project.board.domain.bookmark.entity.Bookmark;
import com.project.board.domain.bookmark.repository.BookmarkRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndPost(userId, postId);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false; // 북마크 취소됨
        } else {
            bookmarkRepository.save(new Bookmark(user, post));
            return true; // 북마크 추가됨
        }
    }
}
