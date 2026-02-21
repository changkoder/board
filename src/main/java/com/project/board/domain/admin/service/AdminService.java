package com.project.board.domain.admin.service;

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
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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

    // 숨김 게시글 목록
    public List<PostListResponse> getHiddenPosts() {
        List<Post> posts = postRepository.findByHiddenTrueAndDeletedFalse();
        return posts.stream()
                .map(PostListResponse::from)
                .toList();
    }

    // 숨김 댓글 목록
    public List<CommentResponse> getHiddenComments() {
        List<Comment> comments = commentRepository.findByHiddenTrueAndDeletedFalse();
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    // 게시글 숨기기
    @Transactional
    public void hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.hide();
    }

    // 댓글 숨기기
    @Transactional
    public void hideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.hide();
    }

    // 게시글 숨김 해제
    @Transactional
    public void restorePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.restore();
    }

    // 댓글 숨김 해제
    @Transactional
    public void restoreComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.restore();
    }

    // 게시글 강제 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.delete();
        post.getUser().decreasePostCount();
    }

    // 댓글 강제 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
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
    public List<UserResponse> getBlockedUsers() {
        List<User> users = userRepository.findByStatus(User.Status.BLOCKED);
        return users.stream()
                .map(UserResponse::new)
                .toList();
    }
}