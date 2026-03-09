package com.project.board.domain.report.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.report.dto.ReportRequest;
import com.project.board.domain.report.entity.Report;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReportServiceTest {

    @Autowired
    private ReportService reportService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private User reporter;
    private User author;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        reporter = userRepository.save(User.builder()
                .email("reporter@test.com")
                .password("password")
                .nickname("reporter")
                .build());

        author = userRepository.save(User.builder()
                .email("author@test.com")
                .password("password")
                .nickname("author")
                .build());

        Category category = categoryRepository.save(new Category("자유"));

        post = postRepository.save(Post.builder()
                .user(author)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build());

        comment = commentRepository.save(Comment.builder()
                .post(post)
                .user(author)
                .content("테스트 댓글")
                .build());
    }

    private ReportRequest createReportRequest() {
        ReportRequest request = new ReportRequest();
        ReflectionTestUtils.setField(request, "reason", Report.ReportReason.SPAM);
        return request;
    }

    @Test
    @DisplayName("게시글 신고 성공")
    void reportPost_success() {
        // when & then
        assertDoesNotThrow(() -> reportService.reportPost(reporter.getId(), post.getId(), createReportRequest()));
    }

    @Test
    @DisplayName("게시글 중복 신고 시 예외")
    void reportPost_duplicate() {
        // given
        reportService.reportPost(reporter.getId(), post.getId(), createReportRequest());

        // when & then
        assertThatThrownBy(() -> reportService.reportPost(reporter.getId(), post.getId(), createReportRequest()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("댓글 신고 성공")
    void reportComment_success() {
        // when & then
        assertDoesNotThrow(() -> reportService.reportComment(reporter.getId(), comment.getId(), createReportRequest()));
    }

    @Test
    @DisplayName("댓글 중복 신고 시 예외")
    void reportComment_duplicate() {
        // given
        reportService.reportComment(reporter.getId(), comment.getId(), createReportRequest());

        // when & then
        assertThatThrownBy(() -> reportService.reportComment(reporter.getId(), comment.getId(), createReportRequest()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("본인 게시글 신고 시 예외")
    void reportPost_own_throwsException() {
        // when & then
        assertThatThrownBy(() -> reportService.reportPost(author.getId(), post.getId(), createReportRequest()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("본인 댓글 신고 시 예외")
    void reportComment_own_throwsException() {
        // when & then
        assertThatThrownBy(() -> reportService.reportComment(author.getId(), comment.getId(), createReportRequest()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("게시글 5회 신고 시 자동 숨김")
    void reportPost_autoHide() {
        // given
        for (int i = 0; i < 5; i++) {
            User reporterN = userRepository.save(User.builder()
                    .email("reporter" + i + "@test.com")
                    .password("password")
                    .nickname("reporter" + i)
                    .build());
            reportService.reportPost(reporterN.getId(), post.getId(), createReportRequest());
        }

        // then
        Post reportedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reportedPost.isHidden()).isTrue();
    }
}
