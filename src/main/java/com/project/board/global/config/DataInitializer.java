package com.project.board.global.config;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
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
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        Category category1 = categoryRepository.save(new Category("자유"));
        Category category2 = categoryRepository.save(new Category("질문"));
        Category category3 = categoryRepository.save(new Category("정보공유"));

        User user = userRepository.save(User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("테스트유저")
                .build());

        postRepository.save(Post.builder()
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
    }
}
