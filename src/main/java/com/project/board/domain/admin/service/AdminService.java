package com.project.board.domain.admin.service;

import com.project.board.domain.comment.dto.AdminCommentDetailResponse;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.dto.AdminPostDetailResponse;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.report.dto.ReportSummaryResponse;
import com.project.board.domain.report.repository.ReportRepository;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // 숨김 게시글 목록
    public Page<PostListResponse> getHiddenPosts(Pageable pageable) {
        return postRepository.findHiddenPosts(pageable)
                .map(post -> PostListResponse.from(post));
    }

    // 숨김 댓글 목록
    public Page<CommentResponse> getHiddenComments(Pageable pageable) {
        return commentRepository.findHiddenComments(pageable)
                .map(comment -> CommentResponse.from(comment));
    }

    // 관리자 게시글 상세 (숨김/삭제 여부 무관 + 신고 내역 포함)
    public AdminPostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findByIdWithDetailsIncludingHidden(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<ReportSummaryResponse> reports = reportRepository.findAllByPostIdWithReporter(postId).stream()
                .map(report -> ReportSummaryResponse.from(report))
                .toList();

        return AdminPostDetailResponse.from(post, reports);
    }

    // 관리자 댓글 상세 (숨김/삭제 여부 무관 + 신고 내역 포함)
    public AdminCommentDetailResponse getCommentDetail(Long commentId) {
        Comment comment = commentRepository.findByIdWithDetailsIncludingHidden(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        List<ReportSummaryResponse> reports = reportRepository.findAllByCommentIdWithReporter(commentId).stream()
                .map(report -> ReportSummaryResponse.from(report))
                .toList();

        return AdminCommentDetailResponse.from(comment, reports);
    }

    // 게시글 숨기기
    @Transactional
    public void hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if (post.isHidden()) {
            throw new CustomException(ErrorCode.ALREADY_HIDDEN);
        }

        post.hide();
    }

    // 댓글 숨기기
    @Transactional
    public void hideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (comment.isHidden()) {
            throw new CustomException(ErrorCode.ALREADY_HIDDEN);
        }

        comment.hide();
    }

    // 게시글 숨김 해제
    @Transactional
    public void restorePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if (!post.isHidden()) {
            throw new CustomException(ErrorCode.NOT_HIDDEN);
        }

        post.restore();
    }

    // 댓글 숨김 해제
    @Transactional
    public void restoreComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!comment.isHidden()) {
            throw new CustomException(ErrorCode.NOT_HIDDEN);
        }

        comment.restore();
    }

    // 게시글 강제 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        post.delete();
        post.getUser().decreasePostCount();
    }

    // 댓글 강제 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        comment.delete();
        comment.getPost().decreaseCommentCount();
    }

    // 회원 차단
    @Transactional
    public void blockUser(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == User.Role.ADMIN) {
            throw new CustomException(ErrorCode.CANNOT_BLOCK_ADMIN);
        }

        if (user.getStatus() == User.Status.BLOCKED) {
            throw new CustomException(ErrorCode.ALREADY_BLOCKED);
        }

        user.block();
    }

    // 회원 차단 해제
    @Transactional
    public void unblockUser(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != User.Status.BLOCKED) {
            throw new CustomException(ErrorCode.NOT_BLOCKED);
        }

        user.unblock();
    }

    // 차단 회원 목록
    public Page<UserResponse> getBlockedUsers(Pageable pageable) {
        return userRepository.findByStatus(User.Status.BLOCKED, pageable)
                .map(UserResponse::new);
    }
}