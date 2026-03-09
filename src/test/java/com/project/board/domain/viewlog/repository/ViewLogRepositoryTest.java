package com.project.board.domain.viewlog.repository;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.domain.viewlog.entity.ViewLog;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ViewLogRepositoryTest {

    @Autowired
    private ViewLogRepository viewLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

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
    @DisplayName("조회 기록이 있으면 true 반환")
    void existsByUserAndPost_true() {
        // given
        viewLogRepository.save(new ViewLog(user, post));

        em.flush();
        em.clear();

        // when
        boolean result = viewLogRepository.existsByUserAndPost(user.getId(), post.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조회 기록이 없으면 false 반환")
    void existsByUserAndPost_false() {
        // when
        boolean result = viewLogRepository.existsByUserAndPost(user.getId(), post.getId());

        // then
        assertThat(result).isFalse();
    }
}
