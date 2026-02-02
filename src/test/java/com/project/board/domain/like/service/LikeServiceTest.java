package com.project.board.domain.like.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class LikeServiceTest {

    @Autowired
    private LikeService likeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Post post;
    private Category category;

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
    }

    @Test
    @DisplayName("게시글 좋아요 추가 시 likeCount 증가")
    void togglePostLike_add() {
        // when
        boolean result = likeService.togglePostLike(user.getId(), post.getId());

        // then
        assertThat(result).isTrue();
        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 시 likeCount 감소")
    void togglePostLike_cancel() {
        // given
        likeService.togglePostLike(user.getId(), post.getId()); // 먼저 좋아요

        // when
        boolean result = likeService.togglePostLike(user.getId(), post.getId()); // 취소

        // then
        assertThat(result).isFalse();
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 좋아요 추가 시 likeCount 증가")
    void toggleCommentLike_add() {
        // given
        Comment comment = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("테스트 댓글")
                .build());

        // when
        boolean result = likeService.toggleCommentLike(user.getId(), comment.getId());

        // then
        assertThat(result).isTrue();
        assertThat(comment.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 likeCount 감소")
    void toggleCommentLike_cancel() {
        // given
        Comment comment = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("테스트 댓글")
                .build());
        likeService.toggleCommentLike(user.getId(), comment.getId()); // 먼저 좋아요

        // when
        boolean result = likeService.toggleCommentLike(user.getId(), comment.getId()); // 취소

        // then
        assertThat(result).isFalse();
        assertThat(comment.getLikeCount()).isEqualTo(0);
    }
}