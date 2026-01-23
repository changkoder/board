package com.project.board.domain.post.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.post.dto.PostCreateRequest;
import com.project.board.domain.post.dto.PostResponse;
import com.project.board.domain.post.dto.PostUpdateRequest;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.entity.PostImage;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Page<PostResponse> findAll(Long categoryId, Pageable pageable){
        Page<Post> posts;

        if(categoryId != null){
            posts = postRepository.findByCategoryActive(categoryId, pageable);
        } else {
            posts = postRepository.findAllActive(pageable);
        }

        return posts.map(post -> PostResponse.from(post));
    }

    @Transactional
    public PostResponse create(Long userId, PostCreateRequest request){//서비스 계층이 받는 정보는 주로 아이디만 받나보군
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Post post = Post.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        if(request.getImageUrls() != null){
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PostImage image = PostImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .imageOrder(i)
                        .build();
                post.addImage(image);
            }
        }

        postRepository.save(post);
        user.increasePostCount();

        return PostResponse.from(post);
    }


    public PostResponse findById(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        //카테고리는 수정 못하나?

        post.update(request.getTitle(), request.getContent(), category);

        post.clearImages();
        //만약 수정창에서 사용자가 이미지를 그대로 뒀어도, 다 클리어하고 새로 업로드 하는 형식인건가
        //즉 사용자가 이미지를 그대로 둬도 같은 이미지로 요청이 다시 오는 방식인건가
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PostImage image = PostImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .imageOrder(i)
                        .build();
                post.addImage(image);
            }
        }
            return PostResponse.from(post);
    }
    // 게시글 삭제

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.delete();
        post.getUser().decreasePostCount();
    }
}
