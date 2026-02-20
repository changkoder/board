package com.project.board.domain.user.service;

import com.project.board.domain.bookmark.entity.Bookmark;
import com.project.board.domain.bookmark.repository.BookmarkRepository;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.entity.PostLike;
import com.project.board.domain.like.repository.PostLikeRepository;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.dto.PasswordChangeRequest;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.dto.UserUpdateRequest;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;

    public UserResponse getMyInfo(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateMyInfo(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(request.getNickname(), request.getProfileImg());
        return new UserResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.delete();
    }

    public List<PostListResponse> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(PostListResponse::from)
                .toList();
    }

    public List<CommentResponse> getMyComments(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    public List<PostListResponse> getMyLikedPosts(Long userId) {
        List<PostLike> likes = postLikeRepository.findByUserIdWithPost(userId);
        return likes.stream()
                .map(like -> PostListResponse.from(like.getPost()))
                .toList();
    }

    public List<PostListResponse> getMyBookmarkedPosts(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserIdWithPost(userId);
        return bookmarks.stream()
                .map(bookmark -> PostListResponse.from(bookmark.getPost()))
                .toList();
    }
}
