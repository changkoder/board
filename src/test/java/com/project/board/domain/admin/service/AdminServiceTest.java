package com.project.board.domain.admin.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.dto.AdminCommentDetailResponse;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.dto.AdminPostDetailResponse;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.report.entity.Report;
import com.project.board.domain.report.repository.ReportRepository;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
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
    @Autowired
    private ReportRepository reportRepository;

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
        Page<UserResponse> result = adminService.getBlockedUsers(PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
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

    @Test
    @DisplayName("관리자 게시글 상세 조회 - 숨김 게시글도 조회된다")
    void getPostDetail_hiddenPost_success() {
        // given
        adminService.hidePost(post.getId());

        // when
        AdminPostDetailResponse response = adminService.getPostDetail(post.getId());

        // then
        assertThat(response.getId()).isEqualTo(post.getId());
        assertThat(response.isHidden()).isTrue();
        assertThat(response.getReports()).isEmpty();
    }

    @Test
    @DisplayName("관리자 게시글 상세 조회 - 신고 내역이 함께 반환된다")
    void getPostDetail_withReports_success() {
        // given
        User reporter = userRepository.save(User.builder()
                .email("reporter@test.com")
                .password("password")
                .nickname("reporter")
                .build());

        reportRepository.save(Report.builder()
                .user(reporter)
                .post(post)
                .reason(Report.ReportReason.ABUSE)
                .build());

        // when
        AdminPostDetailResponse response = adminService.getPostDetail(post.getId());

        // then
        assertThat(response.getReports()).hasSize(1);
        assertThat(response.getReports().get(0).getReporterNickname()).isEqualTo("reporter");
        assertThat(response.getReports().get(0).getReason()).isEqualTo(Report.ReportReason.ABUSE);
        assertThat(response.getReports().get(0).getReasonLabel()).isEqualTo("욕설/비하");
    }

    @Test
    @DisplayName("관리자 게시글 상세 조회 - 존재하지 않는 게시글이면 예외")
    void getPostDetail_notFound_throwsException() {
        assertThatThrownBy(() -> adminService.getPostDetail(9999L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("관리자 댓글 상세 조회 - 숨김 댓글도 조회된다")
    void getCommentDetail_hiddenComment_success() {
        // given
        adminService.hideComment(comment.getId());

        // when
        AdminCommentDetailResponse response = adminService.getCommentDetail(comment.getId());

        // then
        assertThat(response.getId()).isEqualTo(comment.getId());
        assertThat(response.isHidden()).isTrue();
        assertThat(response.getContent()).isEqualTo(comment.getContent());
        assertThat(response.getAuthorNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("관리자 댓글 상세 조회 - 대댓글(parent 있음)도 조회된다")
    void getCommentDetail_reply_success() {
        // given
        Comment reply = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .parent(comment)
                .content("대댓글 내용")
                .build());

        // when
        AdminCommentDetailResponse response = adminService.getCommentDetail(reply.getId());

        // then
        assertThat(response.getId()).isEqualTo(reply.getId());
        assertThat(response.getContent()).isEqualTo("대댓글 내용");
    }

    @Test
    @DisplayName("관리자 댓글 상세 조회 - 신고 내역이 함께 반환된다")
    void getCommentDetail_withReports_success() {
        // given
        User reporter = userRepository.save(User.builder()
                .email("reporter@test.com")
                .password("password")
                .nickname("reporter")
                .build());

        reportRepository.save(Report.builder()
                .user(reporter)
                .comment(comment)
                .reason(Report.ReportReason.SPAM)
                .build());

        // when
        AdminCommentDetailResponse response = adminService.getCommentDetail(comment.getId());

        // then
        assertThat(response.getReports()).hasSize(1);
        assertThat(response.getReports().get(0).getReason()).isEqualTo(Report.ReportReason.SPAM);
        assertThat(response.getReports().get(0).getReasonLabel()).isEqualTo("스팸/광고");
    }

    @Test
    @DisplayName("관리자 댓글 상세 조회 - 존재하지 않는 댓글이면 예외")
    void getCommentDetail_notFound_throwsException() {
        assertThatThrownBy(() -> adminService.getCommentDetail(9999L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getMessage());
    }
}
