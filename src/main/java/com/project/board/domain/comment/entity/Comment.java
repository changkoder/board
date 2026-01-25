package com.project.board.domain.comment.entity;

import com.project.board.domain.post.entity.Post;
import com.project.board.domain.user.entity.User;
import com.project.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)// 생각해보니 조인 칼럼에서 네임 속성은 해당 필드의 디비에서의 이름을 정하는건가?
    private Post post;// 어떤 필드는 nullable = false를 해주고 어떤 필드는 그걸 명시 안하는데 이유가 뭐지 디폴트 값이 뭐지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")//nullable = true 해야하는거 아닌가
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private boolean hidden;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    public Comment(Post post, User user, Comment parent, String content) {
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.content = content;
        this.likeCount = 0;
        this.hidden = false;
        this.deleted = false;
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.deleted = true;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void hide() {
        this.hidden = true;
    }

    public void restore() {
        this.hidden = false;
    }
}
