package com.project.board.domain.comment.dto;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentResponseTest {

    private Comment comment;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();

        Category category = new Category("자유");

        Post post = Post.builder()
                .user(user)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build();

        comment = Comment.builder()
                .post(post)
                .user(user)
                .content("테스트 댓글")
                .build();
    }

    @Test
    @DisplayName("from(comment) 호출 시 liked=false 기본값")
    void from_single_defaultLikedFalse() {
        CommentResponse response = CommentResponse.from(comment);
        assertThat(response.isLiked()).isFalse();
    }

    @Test
    @DisplayName("from(comment, children) 호출 시 liked=false 기본값")
    void from_withChildren_defaultLikedFalse() {
        CommentResponse response = CommentResponse.from(comment, List.of());
        assertThat(response.isLiked()).isFalse();
    }

    @Test
    @DisplayName("from(comment, children, true) 호출 시 liked=true")
    void from_withLikedTrue() {
        CommentResponse response = CommentResponse.from(comment, List.of(), true);
        assertThat(response.isLiked()).isTrue();
    }

    @Test
    @DisplayName("from(comment, children, false) 호출 시 liked=false")
    void from_withLikedFalse() {
        CommentResponse response = CommentResponse.from(comment, List.of(), false);
        assertThat(response.isLiked()).isFalse();
    }
}
