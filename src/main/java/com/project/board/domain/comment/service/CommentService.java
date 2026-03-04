package com.project.board.domain.comment.service;

import com.project.board.domain.comment.dto.CommentCreateRequest;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.dto.CommentUpdateRequest;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.notification.entity.Notification;
import com.project.board.domain.notification.service.NotificationService;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    //멘션 파싱용 정규식
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\S+)");


    public List<CommentResponse> findByPostId(Long postId){
        List<Comment> parents = commentRepository.findParentsByPostId(postId);

        return parents.stream()
                .map(parent -> {
                    List<Comment> children = commentRepository.findChildrenByParentId(parent.getId());
                    List<CommentResponse> childResponses = children.stream()
                            .map(child -> CommentResponse.from(child))
                            .toList();

                    return CommentResponse.from(parent, childResponses);
                })
                .toList();
    }

    @Transactional
    public CommentResponse create(Long postId, Long userId, CommentCreateRequest request){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(post.isDeleted()){
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if(post.isHidden()){
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment parent = null;
        if(request.getParentId() != null){
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            if(parent.isDeleted()){
                throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
            }
            if(parent.isHidden()){
                throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
            }

            // 대대댓글 방지: 부모가 이미 대댓글이면 부모의 부모를 parent로
            if(parent.getParent() != null){
               parent = parent.getParent();
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parent(parent)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
        post.increaseCommentCount();

        if (parent != null) {
            notificationService.notify(
                    parent.getUser(),
                    Notification.NotificationType.REPLY,
                    post.getId(),
                    parent.getId(),
                    userId,
                    user.getNickname() + "님이 회원님의 댓글에 답글을 남겼습니다."
            );
        } else {
            notificationService.notify(
                    post.getUser(),
                    Notification.NotificationType.COMMENT,
                    post.getId(),
                    comment.getId(),
                    userId,
                    user.getNickname() + "님이 회원님의 글에 댓글을 남겼습니다."
            );
        }

        processMentions(comment, user, post);

        return CommentResponse.from(comment);
    }

    private void processMentions(Comment comment, User author, Post post) {
        Set<String> nicknames = extractMentions(comment.getContent());

        for (String nickname : nicknames) {
            userRepository.findByNickname(nickname).ifPresent(mentionedUser -> {
                if (mentionedUser.getId().equals(author.getId())) {
                    return;
                }

                notificationService.notify(
                        mentionedUser,
                        Notification.NotificationType.MENTION,
                        post.getId(),
                        comment.getId(),
                        author.getId(),
                        author.getNickname() + "님이 댓글에서 회원님을 멘션했습니다."
                );
            });
        }
    }

    private Set<String> extractMentions(String content) {
        LinkedHashSet<String> nicknames = new LinkedHashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);

        while (matcher.find()) {
            nicknames.add(matcher.group(1));
        }
        return nicknames;
    }

    @Transactional
    public CommentResponse update(Long commentId, Long userId, CommentUpdateRequest request){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if(comment.isDeleted()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if(comment.isHidden()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.update(request.getContent());

        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if(comment.isDeleted()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if(comment.isHidden()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.delete();
        comment.getPost().decreaseCommentCount();
    }
}
