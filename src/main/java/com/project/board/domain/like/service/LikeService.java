package com.project.board.domain.like.service;

import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.entity.CommentLike;
import com.project.board.domain.like.entity.PostLike;
import com.project.board.domain.like.repository.CommentLikeRepository;
import com.project.board.domain.like.repository.PostLikeRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean togglePostLike(Long userId, Long postId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(userId, postId);

        if(existingLike.isPresent()){
            postLikeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            return false; // 좋아요 취소됨
        } else {
            postLikeRepository.save(new PostLike(user, post));
            post.increaseLikeCount();
            return true; // 좋아요 추가됨
        }
    }

    @Transactional
    public boolean toggleCommentLike(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Optional<CommentLike> existingLike = commentLikeRepository.findByUserAndComment(userId, commentId);

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            comment.decreaseLikeCount();
            return false; // 좋아요 취소됨
        } else {
            commentLikeRepository.save(new CommentLike(user, comment));
            comment.increaseLikeCount();
            return true; // 좋아요 추가됨
        }
    }
}
