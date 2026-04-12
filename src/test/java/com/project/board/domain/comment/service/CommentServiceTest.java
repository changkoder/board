package com.project.board.domain.comment.service;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.dto.CommentCreateRequest;
import com.project.board.domain.comment.dto.CommentResponse;
import com.project.board.domain.comment.dto.CommentUpdateRequest;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.entity.CommentLike;
import com.project.board.domain.like.repository.CommentLikeRepository;
import com.project.board.domain.like.service.LikeService;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.global.exception.CustomException;
import com.project.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private LikeService likeService;

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
    @DisplayName("댓글 작성 시 commentCount 증가")
    void create_increasesCommentCount() {
        // given
        CommentCreateRequest request = createCommentRequest("테스트 댓글", null);

        // when
        commentService.create(post.getId(), user.getId(), request);

        // then
        assertThat(post.getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 삭제 시 commentCount 감소")
    void delete_decreasesCommentCount() {
        // given
        CommentCreateRequest request = createCommentRequest("테스트 댓글", null);
        CommentResponse comment = commentService.create(post.getId(), user.getId(), request);

        // when
        commentService.delete(comment.getId(), user.getId());

        // then
        assertThat(post.getCommentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("대댓글 작성 시 parentId가 설정됨")
    void create_reply_setParentId(){
        //given
        CommentCreateRequest parentRequest = createCommentRequest("부모댓글", null);
        CommentResponse parentComment = commentService.create(post.getId(), user.getId(), parentRequest);

        CommentCreateRequest replyRequest = createCommentRequest("대댓글", parentComment.getId());

        // when
        CommentResponse reply = commentService.create(post.getId(), user.getId(), replyRequest);

        // then
        Comment savedReply = commentRepository.findById(reply.getId()).orElseThrow();
        assertThat(savedReply.getParent()).isNotNull();
        assertThat(savedReply.getParent().getId()).isEqualTo(parentComment.getId());
    }

    @Test
    @DisplayName("대대댓글 작성 시 부모의 부모로 연결됨 (depth 2 유지)")
    void create_replyToReply_connectsToGrandparent() {
        // given
        CommentCreateRequest parentRequest = createCommentRequest("부모 댓글", null);
        CommentResponse parentComment = commentService.create(post.getId(), user.getId(), parentRequest);

        CommentCreateRequest replyRequest = createCommentRequest("대댓글", parentComment.getId());
        CommentResponse reply = commentService.create(post.getId(), user.getId(), replyRequest);

        CommentCreateRequest replyToReplyRequest = createCommentRequest("대대댓글", reply.getId());

        // when
        CommentResponse replyToReply = commentService.create(post.getId(), user.getId(), replyToReplyRequest);

        // then
        Comment savedReplyToReply = commentRepository.findById(replyToReply.getId()).orElseThrow();
        assertThat(savedReplyToReply.getParent().getId()).isEqualTo(parentComment.getId());
    }

    @Test
    @DisplayName("타인 댓글 수정 시 예외 발생")
    void update_otherUserComment_throwsException() {
        // given
        CommentCreateRequest request = createCommentRequest("테스트 댓글", null);
        CommentResponse comment = commentService.create(post.getId(), user.getId(), request);

        User otherUser = userRepository.save(User.builder()
                .email("other@test.com")
                .password("password")
                .nickname("other")
                .build());

        CommentUpdateRequest updateRequest = new CommentUpdateRequest();
        ReflectionTestUtils.setField(updateRequest, "content", "수정된 댓글");

        // when & then
        assertThatThrownBy(() -> commentService.update(comment.getId(), otherUser.getId(), updateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("타인 댓글 삭제 시 예외 발생")
    void delete_otherUserComment_throwsException() {
        // given
        CommentCreateRequest request = createCommentRequest("테스트 댓글", null);
        CommentResponse comment = commentService.create(post.getId(), user.getId(), request);

        User otherUser = userRepository.save(User.builder()
                .email("other@test.com")
                .password("password")
                .nickname("other")
                .build());

        // when & then
        assertThatThrownBy(() -> commentService.delete(comment.getId(), otherUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void update_success() {
        // given
        CommentCreateRequest request = createCommentRequest("원본 댓글", null);
        CommentResponse comment = commentService.create(post.getId(), user.getId(), request);

        CommentUpdateRequest updateRequest = new CommentUpdateRequest();
        ReflectionTestUtils.setField(updateRequest, "content", "수정된 댓글");

        // when
        CommentResponse response = commentService.update(comment.getId(), user.getId(), updateRequest);

        // then
        assertThat(response.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("게시글의 댓글 목록 조회")
    void findByPostId_success() {
        // given
        commentService.create(post.getId(), user.getId(), createCommentRequest("댓글1", null));
        commentService.create(post.getId(), user.getId(), createCommentRequest("댓글2", null));
        commentService.create(post.getId(), user.getId(), createCommentRequest("댓글3", null));

        // when
        var result = commentService.findByPostId(post.getId(), user.getId());

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("삭제된 댓글은 soft delete")
    void delete_softDelete() {
        // given
        CommentCreateRequest request = createCommentRequest("삭제할 댓글", null);
        CommentResponse comment = commentService.create(post.getId(), user.getId(), request);

        // when
        commentService.delete(comment.getId(), user.getId());

        // then
        Comment deleted = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("로그인 사용자 - 좋아요한 댓글은 liked=true, 안 한 댓글은 liked=false")
    void findByPostId_withLogin_likedStatus() {
        // given
        CommentResponse c1 = commentService.create(post.getId(), user.getId(), createCommentRequest("댓글1", null));
        commentService.create(post.getId(), user.getId(), createCommentRequest("댓글2", null));

        likeService.toggleCommentLike(user.getId(), c1.getId());

        // when
        List<CommentResponse> result = commentService.findByPostId(post.getId(), user.getId());

        // then
        CommentResponse liked = result.stream().filter(c -> c.getId().equals(c1.getId())).findFirst().orElseThrow();
        CommentResponse notLiked = result.stream().filter(c -> !c.getId().equals(c1.getId())).findFirst().orElseThrow();

        assertThat(liked.isLiked()).isTrue();
        assertThat(notLiked.isLiked()).isFalse();
    }

    @Test
    @DisplayName("비로그인(userId=null) - 전부 liked=false")
    void findByPostId_withoutLogin_allLikedFalse() {
        // given
        CommentResponse c1 = commentService.create(post.getId(), user.getId(), createCommentRequest("댓글1", null));
        commentService.create(post.getId(), user.getId(), createCommentRequest("댓글2", null));

        likeService.toggleCommentLike(user.getId(), c1.getId());

        // when
        List<CommentResponse> result = commentService.findByPostId(post.getId(), null);

        // then
        assertThat(result).allMatch(c -> !c.isLiked());
    }

    @Test
    @DisplayName("대댓글(children)도 liked 상태가 정확히 반영됨")
    void findByPostId_childrenLikedStatus() {
        // given
        CommentResponse parent = commentService.create(post.getId(), user.getId(), createCommentRequest("부모", null));
        CommentResponse child = commentService.create(post.getId(), user.getId(), createCommentRequest("자식", parent.getId()));

        likeService.toggleCommentLike(user.getId(), child.getId());

        // when
        List<CommentResponse> result = commentService.findByPostId(post.getId(), user.getId());

        // then
        CommentResponse parentResult = result.get(0);
        assertThat(parentResult.isLiked()).isFalse();
        assertThat(parentResult.getChildren()).hasSize(1);
        assertThat(parentResult.getChildren().get(0).isLiked()).isTrue();
    }

    @Test
    @DisplayName("부모만 좋아요하고 자식은 안 했을 때 각각 정확한 상태")
    void findByPostId_parentLikedChildNot() {
        // given
        CommentResponse parent = commentService.create(post.getId(), user.getId(), createCommentRequest("부모", null));
        commentService.create(post.getId(), user.getId(), createCommentRequest("자식", parent.getId()));

        likeService.toggleCommentLike(user.getId(), parent.getId());

        // when
        List<CommentResponse> result = commentService.findByPostId(post.getId(), user.getId());

        // then
        CommentResponse parentResult = result.get(0);
        assertThat(parentResult.isLiked()).isTrue();
        assertThat(parentResult.getChildren().get(0).isLiked()).isFalse();
    }

    private CommentCreateRequest createCommentRequest(String content, Long parentId) {
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", content);
        ReflectionTestUtils.setField(request, "parentId", parentId);
        return request;
    }
}