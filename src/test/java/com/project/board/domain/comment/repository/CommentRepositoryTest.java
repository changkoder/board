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
    @DisplayName("전체 댓글 조회시 User fetch join으로 N+1 방지")
    void findAllByPostId_fetchJoinUser_noNPlusOne(){
        // given
        Comment parent1 = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("부모 댓글 1")
                .build());

        Comment parent2 = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("부모 댓글 2")
                .build());

        for (int i = 0; i < 3; i++) {
            commentRepository.save(Comment.builder()
                    .post(post)
                    .user(user)
                    .content("자식 댓글 " + i)
                    .parent(parent1)
                    .build());
        }

        em.flush();
        em.clear();

        // when
        List<Comment> all = commentRepository.findAllByPostId(post.getId());

        // then
        assertThat(all).hasSize(5); // 부모 2 + 자식 3
        all.forEach(c -> assertThat(c.getUser().getNickname()).isEqualTo("tester"));
    }

    @Test
    @DisplayName("부모 댓글이 자식 댓글보다 먼저 정렬됨")
    void findAllByPostId_parentBeforeChildren() {
        // given
        Comment parent = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("부모 댓글")
                .build());

        commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("자식 댓글")
                .parent(parent)
                .build());

        em.flush();
        em.clear();

        // when
        List<Comment> all = commentRepository.findAllByPostId(post.getId());

        // then
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getParent()).isNull(); // 부모가 먼저
        assertThat(all.get(1).getParent()).isNotNull(); // 자식이 나중
    }

    @Test
    @DisplayName("삭제된 댓글은 조회되지 않음")
    void findAllByPostId_excludesDeleted() {
        // given
        commentRepository.save(Comment.builder()
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
        List<Comment> comments = commentRepository.findAllByPostId(post.getId());

        // then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("일반 댓글");
    }
}
