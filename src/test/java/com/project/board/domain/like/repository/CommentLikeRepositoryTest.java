package com.project.board.domain.like.repository;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.entity.CommentLike;
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

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentLikeRepositoryTest {

    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private User otherUser;
    private Post post;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build());

        otherUser = userRepository.save(User.builder()
                .email("other@test.com")
                .password("password")
                .nickname("other")
                .build());

        Category category = categoryRepository.save(new Category("자유"));

        post = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build());

        comment1 = commentRepository.save(Comment.builder()
                .post(post).user(user).content("댓글1").build());
        comment2 = commentRepository.save(Comment.builder()
                .post(post).user(user).content("댓글2").build());
        comment3 = commentRepository.save(Comment.builder()
                .post(post).user(user).content("댓글3").build());
    }

    @Test
    @DisplayName("좋아요한 댓글 ID만 정확히 반환")
    void findLikedCommentIds_returnsLikedOnly() {
        // given
        commentLikeRepository.save(new CommentLike(user, comment1));
        commentLikeRepository.save(new CommentLike(user, comment3));

        // when
        Set<Long> result = commentLikeRepository.findLikedCommentIds(
                user.getId(), List.of(comment1.getId(), comment2.getId(), comment3.getId()));

        // then
        assertThat(result).containsExactlyInAnyOrder(comment1.getId(), comment3.getId());
        assertThat(result).doesNotContain(comment2.getId());
    }

    @Test
    @DisplayName("좋아요 없으면 빈 Set 반환")
    void findLikedCommentIds_noLikes_returnsEmpty() {
        // when
        Set<Long> result = commentLikeRepository.findLikedCommentIds(
                user.getId(), List.of(comment1.getId(), comment2.getId()));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 사용자의 좋아요는 포함되지 않음")
    void findLikedCommentIds_excludesOtherUserLikes() {
        // given
        commentLikeRepository.save(new CommentLike(otherUser, comment1));
        commentLikeRepository.save(new CommentLike(otherUser, comment2));

        // when
        Set<Long> result = commentLikeRepository.findLikedCommentIds(
                user.getId(), List.of(comment1.getId(), comment2.getId()));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 commentIds 입력 시 빈 Set 반환")
    void findLikedCommentIds_emptyInput_returnsEmpty() {
        // given
        commentLikeRepository.save(new CommentLike(user, comment1));

        // when
        Set<Long> result = commentLikeRepository.findLikedCommentIds(
                user.getId(), List.of());

        // then
        assertThat(result).isEmpty();
    }
}
