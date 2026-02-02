package com.project.board.domain.post.repository;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.post.dto.PostSearchCondition;
import com.project.board.domain.post.dto.SearchType;
import com.project.board.domain.post.entity.Post;
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

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build());

        category = categoryRepository.save(new Category("자유"));
    }

    @Test
    @DisplayName("no-offset 페이지네이션 - 첫 페이지")
    void findAllNoOffset_firstPage() {
        // given
        for (int i = 1; i <= 10; i++) {
            postRepository.save(Post.builder()
                    .user(user)
                    .category(category)
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .build());
        }

        em.flush();
        em.clear();

        // when
        List<Post> posts = postRepository.findAllNoOffset(null, 5);

        // then
        assertThat(posts).hasSize(5);
        assertThat(posts.get(0).getTitle()).isEqualTo("게시글 10"); // 최신순
    }

    @Test
    @DisplayName("no-offset 페이지네이션 - 다음 페이지")
    void findAllNoOffset_nextPage() {
        // given
        for (int i = 1; i <= 10; i++) {
            postRepository.save(Post.builder()
                    .user(user)
                    .category(category)
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .build());
        }

        em.flush();
        em.clear();

        List<Post> firstPage = postRepository.findAllNoOffset(null, 5);
        Long lastPostId = firstPage.get(firstPage.size() - 1).getId();

        // when
        List<Post> secondPage = postRepository.findAllNoOffset(lastPostId, 5);

        // then
        assertThat(secondPage).hasSize(5);
        assertThat(secondPage.get(0).getId()).isLessThan(lastPostId);
    }

    @Test
    @DisplayName("삭제된 게시글은 조회되지 않음")
    void findAllNoOffset_excludesDeleted() {
        // given
        Post normal = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("일반 게시글")
                .content("내용")
                .build());

        Post deleted = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("삭제된 게시글")
                .content("내용")
                .build());
        deleted.delete();

        em.flush();
        em.clear();

        // when
        List<Post> posts = postRepository.findAllNoOffset(null, 10);

        // then
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("일반 게시글");
    }

    @Test
    @DisplayName("숨김 게시글은 조회되지 않음")
    void findAllNoOffset_excludesHidden() {
        // given
        Post normal = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("일반 게시글")
                .content("내용")
                .build());

        Post hidden = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("숨김 게시글")
                .content("내용")
                .build());
        hidden.hide();

        em.flush();
        em.clear();

        // when
        List<Post> posts = postRepository.findAllNoOffset(null, 10);

        // then
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("일반 게시글");
    }

    @Test
    @DisplayName("제목+내용 검색")
    void search_titleContent() {
        // given
        postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("스프링 부트 강좌")
                .content("JPA 내용")
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("리액트 강좌")
                .content("스프링 연동")
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("자바 기초")
                .content("변수와 타입")
                .build());

        em.flush();
        em.clear();

        PostSearchCondition condition = new PostSearchCondition();
        condition.setSearchType(SearchType.TITLE_CONTENT);
        condition.setKeyword("스프링");

        // when
        Page<Post> result = postRepository.search(condition, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("검색 결과 페이지네이션")
    void search_withPagination() {
        // given
        for (int i = 1; i <= 15; i++) {
            postRepository.save(Post.builder()
                    .user(user)
                    .category(category)
                    .title("스프링 게시글 " + i)
                    .content("내용")
                    .build());
        }

        em.flush();
        em.clear();

        PostSearchCondition condition = new PostSearchCondition();
        condition.setSearchType(SearchType.TITLE);
        condition.setKeyword("스프링");

        // when
        Page<Post> firstPage = postRepository.search(condition, PageRequest.of(0, 10));
        Page<Post> secondPage = postRepository.search(condition, PageRequest.of(1, 10));

        // then
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(firstPage.getTotalElements()).isEqualTo(15);
    }
}