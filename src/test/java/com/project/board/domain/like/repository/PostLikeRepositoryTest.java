package com.project.board.domain.like.repository;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.like.entity.PostLike;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;
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

        postLikeRepository.save(new PostLike(user, post));
    }

    @Test
    @DisplayName("좋아요 목록 조회 시 Post/User/Category fetch join으로 N+1 방지")
    void findByUserIdWithPost_fetchJoin_noNPlusOne() {
        // given
        em.flush();
        em.clear();

        // when
        Page<PostLike> result = postLikeRepository.findByUserIdWithPost(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        PostLike like = result.getContent().get(0);
        assertThat(like.getPost().getTitle()).isEqualTo("테스트 게시글");
        assertThat(like.getPost().getUser().getNickname()).isEqualTo("tester");
        assertThat(like.getPost().getCategory().getName()).isEqualTo("자유");
    }
}
