package com.project.board.domain.user.service;

import com.project.board.domain.bookmark.repository.BookmarkRepository;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.repository.PostLikeRepository;
import com.project.board.domain.post.dto.PostListResponse;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.dto.PasswordChangeRequest;
import com.project.board.domain.user.dto.UserProfileResponse;
import com.project.board.domain.user.dto.UserResponse;
import com.project.board.domain.user.dto.UserUpdateRequest;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }

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

        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == User.Role.ADMIN) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_ADMIN);
        }

        user.delete();
    }

    public Page<PostListResponse> getMyPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserIdActive(userId, pageable)
                .map(PostListResponse::from);
    }

    public Page<CommentResponse> getMyComments(Long userId, Pageable pageable) {
        return commentRepository.findByUserIdActive(userId, pageable)
                .map(CommentResponse::from);
    }

    public Page<PostListResponse> getMyLikedPosts(Long userId, Pageable pageable) {
        return postLikeRepository.findByUserIdWithPost(userId, pageable)
                .map(like -> PostListResponse.from(like.getPost()));
    }

    public Page<PostListResponse> getMyBookmarkedPosts(Long userId, Pageable pageable) {
        return bookmarkRepository.findByUserIdWithPost(userId, pageable)
                .map(bookmark -> PostListResponse.from(bookmark.getPost()));
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return new UserProfileResponse(user);
    }

    public UserResponse getUserProfileByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return new UserResponse(user);
    }

    public Page<PostListResponse> getUserPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return postRepository.findByUserIdActive(userId, pageable)
                .map(PostListResponse::from);
    }
}
