package com.project.board.domain.comment.service;

import com.project.board.domain.comment.dto.CommentCreateRequest;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.dto.CommentUpdateRequest;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment parent = null;
        if(request.getParentId() != null){
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            // 대대댓글 방지: 부모가 이미 대댓글이면 부모의 부모를 parent로
            if(parent.getParent() != null){//이거보단 애초에 대댓글에는 댓글을 달수 없게 하면 되지 않나. 그건 프론트에서 만지면되나
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

        return CommentResponse.from(comment); //자식 목록은 안넣어도되나?
    }

    @Transactional
    public CommentResponse update(Long commentId, CommentUpdateRequest request){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.update(request.getContent());//근데 이런 엔티티안에 만드는 메서드를 뭐라하더라, 그리고 어떤 것들을 주로 엔티티안에 만들지

        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.delete();
        comment.getPost().decreaseCommentCount();
    }
}
