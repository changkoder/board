package com.project.board.domain.bookmark.service;

import com.project.board.domain.bookmark.repository.BookmarkRepository;
import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BookmarkServiceTest {

    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build());

        Category category = categoryRepository.save(new Category("자유"));

        post = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build());
    }

    @Test
    @DisplayName("북마크 추가 (토글 on)")
    void toggleBookmark_add() {
        // when
        boolean result = bookmarkService.toggleBookmark(user.getId(), post.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("북마크 취소 (토글 off)")
    void toggleBookmark_cancel() {
        // given
        bookmarkService.toggleBookmark(user.getId(), post.getId()); // 먼저 북마크

        // when
        boolean result = bookmarkService.toggleBookmark(user.getId(), post.getId()); // 취소

        // then
        assertThat(result).isFalse();
    }
}
