package com.project.board.global.config;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        Category category1 = categoryRepository.save(new Category("자유"));
        Category category2 = categoryRepository.save(new Category("질문"));
        Category category3 = categoryRepository.save(new Category("정보공유"));

        User user = userRepository.save(User.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("테스트유저1")
                .build());

        userRepository.save(User.builder()
                .email("test2@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("테스트유저2")
                .build());

        Post post1 = postRepository.save(Post.builder()
                .user(user)
                .category(category1)
                .title("첫 번째 글")
                .content("자유 게시판 내용입니다")
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .category(category2)
                .title("두 번째 글")
                .content("질문 게시판 내용입니다")
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .category(category3)
                .title("세 번째 글")
                .content("정보공유 게시판 내용입니다")
                .build());

        // 댓글 추가
        Comment comment1 = commentRepository.save(Comment.builder()
                .post(post1)
                .user(user)
                .content("첫 번째 댓글입니다")
                .build());

        Comment comment2 = commentRepository.save(Comment.builder()
                .post(post1)
                .user(user)
                .content("두 번째 댓글입니다")
                .build());

        // 대댓글 추가
        commentRepository.save(Comment.builder()
                .post(post1)
                .user(user)
                .parent(comment1)
                .content("첫 번째 댓글의 대댓글입니다")
                .build());

        commentRepository.save(Comment.builder()
                .post(post1)
                .user(user)
                .parent(comment1)
                .content("첫 번째 댓글의 두 번째 대댓글입니다")
                .build());
    }
}
