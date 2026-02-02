package com.project.board.domain.comment.repository;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build());

        Category category = categoryRepository.save(new Category("자유"));

        post = postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .title("테스트 게시글")
                .content("내용")
                .build());
    }

    @Test
    @DisplayName("부모 댓글 조회시 User fetch join으로 N+1 방지")
    void findParentsByPostId_fetchJoinUser_noNPlusOne(){
        // given
        for (int i = 0; i < 5; i++) {
            commentRepository.save(Comment.builder()
                    .post(post)
                    .user(user)
                    .content("부모 댓글 " + i)
                    .build());
        }

        em.flush();
        em.clear();

        // when
        List<Comment> parents = commentRepository.findParentsByPostId(post.getId());

        // then
        assertThat(parents).hasSize(5);
        // fetch join 동작하면 User 접근 시 추가 쿼리 없음
        parents.forEach(c -> assertThat(c.getUser().getNickname()).isEqualTo("tester"));
    }

    @Test
    @DisplayName("자식 댓글 조회 시 User fetch join으로 N+1 방지")
    void findChildrenByParentId_fetchJoinUser_noNPlusOne() {
        // given
        Comment parent = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("부모 댓글")
                .build());

        for (int i = 0; i < 5; i++) {
            commentRepository.save(Comment.builder()
                    .post(post)
                    .user(user)
                    .content("자식 댓글 " + i)
                    .parent(parent)
                    .build());
        }

        em.flush();
        em.clear();

        // when
        List<Comment> children = commentRepository.findChildrenByParentId(parent.getId());

        // then
        assertThat(children).hasSize(5);
        children.forEach(c -> assertThat(c.getUser().getNickname()).isEqualTo("tester"));
    }

    @Test
    @DisplayName("삭제된 댓글은 조회되지 않음")
    void findParentsByPostId_excludesDeleted() {
        // given
        Comment normal = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("일반 댓글")
                .build());

        Comment deleted = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("삭제된 댓글")
                .build());
        deleted.delete();

        em.flush();
        em.clear();

        // when
        List<Comment> comments = commentRepository.findParentsByPostId(post.getId());

        // then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("일반 댓글");
    }
}