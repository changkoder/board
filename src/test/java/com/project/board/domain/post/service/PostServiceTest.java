package com.project.board.domain.post.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.post.dto.PostCreateRequest;
import com.project.board.domain.post.dto.PostResponse;
import com.project.board.domain.post.dto.PostUpdateRequest;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;

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
    @DisplayName("게시글 작성 시 postCount 증가")
    void create_increasesPostCount() {
        // given
        PostCreateRequest request = createPostRequest("제목", "내용", category.getId());

        // when
        postService.create(user.getId(), request);

        // then
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(foundUser.getPostCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 삭제 시 postCount 감소")
    void delete_decreasesPostCount() {
        // given
        PostCreateRequest request = createPostRequest("제목", "내용", category.getId());
        PostResponse post = postService.create(user.getId(), request);

        // when
        postService.delete(post.getId(), user.getId());

        // then
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(foundUser.getPostCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("타인 글 수정 시 예외 발생")
    void update_otherUserPost_throwsException() {
        // given
        PostCreateRequest request = createPostRequest("제목", "내용", category.getId());
        PostResponse post = postService.create(user.getId(), request);

        User otherUser = userRepository.save(User.builder()
                .email("other@test.com")
                .password("password")
                .nickname("other")
                .build());

        PostUpdateRequest updateRequest = new PostUpdateRequest();
        ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
        ReflectionTestUtils.setField(updateRequest, "content", "수정된 내용");
        ReflectionTestUtils.setField(updateRequest, "categoryId", category.getId());

        // when & then
        assertThatThrownBy(() -> postService.update(post.getId(), otherUser.getId(), updateRequest))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("타인 글 삭제 시 예외 발생")
    void delete_otherUserPost_throwsException() {
        // given
        PostCreateRequest request = createPostRequest("제목", "내용", category.getId());
        PostResponse post = postService.create(user.getId(), request);

        User otherUser = userRepository.save(User.builder()
                .email("other@test.com")
                .password("password")
                .nickname("other")
                .build());

        // when & then
        assertThatThrownBy(() -> postService.delete(post.getId(), otherUser.getId()))
                .isInstanceOf(CustomException.class);
    }

    private PostCreateRequest createPostRequest(String title, String content, Long categoryId) {
        PostCreateRequest request = new PostCreateRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        return request;
    }
}