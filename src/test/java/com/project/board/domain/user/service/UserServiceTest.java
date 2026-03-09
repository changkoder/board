package com.project.board.domain.user.service;

import com.project.board.domain.bookmark.entity.Bookmark;
import com.project.board.domain.bookmark.repository.BookmarkRepository;
import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.entity.PostLike;
import com.project.board.domain.like.repository.PostLikeRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.dto.PasswordChangeRequest;
import com.project.board.domain.user.dto.UserProfileResponse;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.dto.UserUpdateRequest;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("tester")
                .build());

        category = categoryRepository.save(new Category("자유"));
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_success() {
        // given & when
        UserResponse response = userService.getMyInfo(user.getId());

        // then
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 존재하지 않는 유저")
    void getMyInfo_userNotFound() {
        // when & then
        assertThatThrownBy(() -> userService.getMyInfo(999L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("내 정보 수정 성공")
    void updateMyInfo_success() {
        // given
        UserUpdateRequest request = new UserUpdateRequest();
        ReflectionTestUtils.setField(request, "nickname", "newNickname");

        // when
        UserResponse response = userService.updateMyInfo(user.getId(), request);

        // then
        assertThat(response.getNickname()).isEqualTo("newNickname");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_success() {
        // given
        PasswordChangeRequest request = new PasswordChangeRequest();
        ReflectionTestUtils.setField(request, "currentPassword", "password123");
        ReflectionTestUtils.setField(request, "newPassword", "newPassword123");

        // when
        userService.changePassword(user.getId(), request);

        // then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword123", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 틀림")
    void changePassword_invalidCurrentPassword() {
        // given
        PasswordChangeRequest request = new PasswordChangeRequest();
        ReflectionTestUtils.setField(request, "currentPassword", "wrongPassword");
        ReflectionTestUtils.setField(request, "newPassword", "newPassword123");

        // when & then
        assertThatThrownBy(() -> userService.changePassword(user.getId(), request))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 새 비밀번호가 기존과 동일")
    void changePassword_samePassword() {
        // given
        PasswordChangeRequest request = new PasswordChangeRequest();
        ReflectionTestUtils.setField(request, "currentPassword", "password123");
        ReflectionTestUtils.setField(request, "newPassword", "password123");

        // when & then
        assertThatThrownBy(() -> userService.changePassword(user.getId(), request))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 성공 (soft delete)")
    void deleteAccount_success() {
        // when
        userService.deleteAccount(user.getId());

        // then
        User deletedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(deletedUser.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 관리자 계정")
    void deleteAccount_admin() {
        // given
        User admin = userRepository.save(User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("admin")
                .role(User.Role.ADMIN)
                .build());

        // when & then
        assertThatThrownBy(() -> userService.deleteAccount(admin.getId()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("내 게시글 목록 조회")
    void getMyPosts_success() {
        // given
        postRepository.save(Post.builder().user(user).category(category).title("글1").content("내용1").build());
        postRepository.save(Post.builder().user(user).category(category).title("글2").content("내용2").build());

        // when
        var result = userService.getMyPosts(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("내 댓글 목록 조회")
    void getMyComments_success() {
        // given
        Post post = postRepository.save(Post.builder().user(user).category(category).title("글").content("내용").build());
        commentRepository.save(Comment.builder().post(post).user(user).content("댓글1").build());
        commentRepository.save(Comment.builder().post(post).user(user).content("댓글2").build());

        // when
        var result = userService.getMyComments(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("내 좋아요 게시글 목록 조회")
    void getMyLikedPosts_success() {
        // given
        Post post = postRepository.save(Post.builder().user(user).category(category).title("글").content("내용").build());
        postLikeRepository.save(new PostLike(user, post));

        // when
        var result = userService.getMyLikedPosts(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("내 북마크 게시글 목록 조회")
    void getMyBookmarkedPosts_success() {
        // given
        Post post = postRepository.save(Post.builder().user(user).category(category).title("글").content("내용").build());
        bookmarkRepository.save(new Bookmark(user, post));

        // when
        var result = userService.getMyBookmarkedPosts(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("유저 프로필 조회 성공")
    void getUserProfile_success() {
        // when
        UserProfileResponse response = userService.getUserProfile(user.getId());

        // then
        assertThat(response.getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("닉네임으로 유저 프로필 조회 성공")
    void getUserProfileByNickname_success() {
        // when
        UserResponse response = userService.getUserProfileByNickname("tester");

        // then
        assertThat(response.getEmail()).isEqualTo("test@test.com");
    }
}
