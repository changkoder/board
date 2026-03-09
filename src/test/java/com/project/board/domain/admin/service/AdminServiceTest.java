package com.project.board.domain.admin.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
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
class AdminServiceTest {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build());

        category = categoryRepository.save(new Category("자유"));

        post = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build());
        user.increasePostCount();

        comment = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("테스트 댓글")
                .build());
        post.increaseCommentCount();
    }

    @Test
    @DisplayName("게시글 숨김 처리")
    void hidePost_success() {
        // when
        adminService.hidePost(post.getId());

        // then
        assertThat(post.isHidden()).isTrue();
    }

    @Test
    @DisplayName("게시글 숨김 해제 (복원)")
    void restorePost_success() {
        // given
        adminService.hidePost(post.getId());

        // when
        adminService.restorePost(post.getId());

        // then
        assertThat(post.isHidden()).isFalse();
    }

    @Test
    @DisplayName("댓글 숨김 처리")
    void hideComment_success() {
        // when
        adminService.hideComment(comment.getId());

        // then
        assertThat(comment.isHidden()).isTrue();
    }

    @Test
    @DisplayName("댓글 숨김 해제 (복원)")
    void restoreComment_success() {
        // given
        adminService.hideComment(comment.getId());

        // when
        adminService.restoreComment(comment.getId());

        // then
        assertThat(comment.isHidden()).isFalse();
    }

    @Test
    @DisplayName("게시글 영구 삭제")
    void deletePost_success() {
        // when
        adminService.deletePost(post.getId());

        // then
        Post deletedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(deletedPost.isDeleted()).isTrue();
        assertThat(user.getPostCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 영구 삭제")
    void deleteComment_success() {
        // when
        adminService.deleteComment(comment.getId());

        // then
        Comment deletedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(deletedComment.isDeleted()).isTrue();
        assertThat(post.getCommentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("유저 차단")
    void blockUser_success() {
        // when
        adminService.blockUser(user.getId());

        // then
        assertThat(user.getStatus()).isEqualTo(User.Status.BLOCKED);
    }

    @Test
    @DisplayName("유저 차단 해제")
    void unblockUser_success() {
        // given
        adminService.blockUser(user.getId());

        // when
        adminService.unblockUser(user.getId());

        // then
        assertThat(user.getStatus()).isEqualTo(User.Status.ACTIVE);
    }

    @Test
    @DisplayName("숨김 게시글 목록 조회")
    void getHiddenPosts_success() {
        // given
        adminService.hidePost(post.getId());

        // when
        Page<PostListResponse> result = adminService.getHiddenPosts(PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("숨김 댓글 목록 조회")
    void getHiddenComments_success() {
        // given
        adminService.hideComment(comment.getId());

        // when
        Page<CommentResponse> result = adminService.getHiddenComments(PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("차단 회원 목록 조회")
    void getBlockedUsers_success() {
        // given
        adminService.blockUser(user.getId());

        // when
        List<UserResponse> result = adminService.getBlockedUsers();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("관리자 차단 시도 시 예외")
    void blockUser_admin_throwsException() {
        // given
        User admin = userRepository.save(User.builder()
                .email("admin@test.com")
                .password("password")
                .nickname("admin")
                .role(User.Role.ADMIN)
                .build());

        // when & then
        assertThatThrownBy(() -> adminService.blockUser(admin.getId()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("이미 차단된 유저 재차단 시 예외")
    void blockUser_alreadyBlocked_throwsException() {
        // given
        adminService.blockUser(user.getId());

        // when & then
        assertThatThrownBy(() -> adminService.blockUser(user.getId()))
                .isInstanceOf(CustomException.class);
    }
}
