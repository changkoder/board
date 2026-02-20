package com.project.board.domain.user.entity;

import com.project.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//아 기본 생성자 만드는 이유가 뭐더라? 무슨 기술 떄문이라 했는데
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    private String profileImg; //여기에는 왜 칼럼 어노테이션 안붙였지? 칼럼 어노테이션 기능이 뭐더라

    @Enumerated(EnumType.STRING)//이 어노테이션 뭐더라
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private int postCount;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    public User(String email, String password, String nickname, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role != null ? role : Role.USER;
        this.status = Status.ACTIVE;
        this.postCount = 0;
        this.deleted = false;
    }

    public void updateProfile(String nickname, String profileImg) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImg != null) {
            this.profileImg = profileImg;
        }
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void delete() {
        this.deleted = true;
    }

    public void increasePostCount() {
        this.postCount++;
    }

    public void decreasePostCount() {
        if (this.postCount > 0) {
            this.postCount--;
        }
    }

    //이넘을 이렇게 내부 클래스로 만들어도 괜찮나? 분리하는게 낫지 않나?
    public enum Role{
        USER, ADMIN;

    }

    public enum Status{
        ACTIVE, BLOCKED;
    }

    public void block() {
        this.status = Status.BLOCKED;
    }

    public void unblock() {
        this.status = Status.ACTIVE;
    }
}
