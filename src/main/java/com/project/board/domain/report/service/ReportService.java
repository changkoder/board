package com.project.board.domain.report.service;

import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.report.dto.ReportRequest;
import com.project.board.domain.report.entity.Report;
import com.project.board.domain.report.repository.ReportRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final int HIDE_THRESHOLD = 5;

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reportPost(Long userId, Long postId, ReportRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 삭제된 게시글 신고 방지
        if (post.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        // 이미 숨겨진 게시글 신고 방지
        if (post.isHidden()) {
            throw new CustomException(ErrorCode.ALREADY_HIDDEN);
        }

        // 본인 글 신고 방지
        if (post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.CANNOT_REPORT_OWN);
        }

        // 중복 신고 방지
        if (reportRepository.existsByUserAndPost(userId, postId)) {
            throw new CustomException(ErrorCode.DUPLICATE_REPORT);
        }

        Report report = Report.builder()
                .user(user)
                .post(post)
                .reason(request.getReason())
                .build();

        reportRepository.save(report);

        // 자동 숨김 처리
        long reportCount = reportRepository.countByPostId(postId);
        if (reportCount >= HIDE_THRESHOLD) {
            post.hide();
        }
    }

    @Transactional
    public void reportComment(Long userId, Long commentId, ReportRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 삭제된 댓글 신고 방지
        if (comment.isDeleted()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        // 이미 숨겨진 댓글 신고 방지
        if (comment.isHidden()) {
            throw new CustomException(ErrorCode.ALREADY_HIDDEN);
        }

        // 본인 댓글 신고 방지
        if (comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.CANNOT_REPORT_OWN);
        }

        // 중복 신고 방지
        if (reportRepository.existsByUserAndComment(userId, commentId)) {
            throw new CustomException(ErrorCode.DUPLICATE_REPORT);
        }

        Report report = Report.builder()
                .user(user)
                .comment(comment)
                .reason(request.getReason())
                .build();

        reportRepository.save(report);

        // 자동 숨김 처리
        long reportCount = reportRepository.countByCommentId(commentId);
        if (reportCount >= HIDE_THRESHOLD) {
            comment.hide();
        }
    }
}