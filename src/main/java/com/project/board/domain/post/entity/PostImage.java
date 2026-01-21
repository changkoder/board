package com.project.board.domain.post.entity;

import com.project.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int imageOrder;

    @Builder
    public PostImage(String imageUrl, int imageOrder) {
        this.imageUrl = imageUrl;
        this.imageOrder = imageOrder;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
