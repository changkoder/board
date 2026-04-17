← [README로 돌아가기](../README.md)

# 엔티티 필드 상세

## User

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| email | String | |
| password | String | BCrypt 암호화 |
| nickname | String | |
| profileImg | String | nullable |
| role | Role (enum) | `USER`, `ADMIN` |
| status | Status (enum) | `ACTIVE`, `BLOCKED` |
| postCount | int | 비정규화 |
| deleted | boolean | soft delete |
| createdAt | LocalDateTime | BaseEntity 상속 |
| updatedAt | LocalDateTime | BaseEntity 상속 |

## Post

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| title | String (max 100) | |
| content | String (TEXT) | |
| viewCount | int | 비정규화 |
| likeCount | int | 비정규화 |
| commentCount | int | 비정규화 |
| hidden | boolean | 관리자 숨김 |
| deleted | boolean | soft delete |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL |
| category_id | Long (FK) | `@ManyToOne(LAZY)` → Category, NOT NULL |
| images | List\<PostImage\> | `@OneToMany(mappedBy, cascade=ALL, orphanRemoval=true)` |
| createdAt | LocalDateTime | BaseEntity 상속 |
| updatedAt | LocalDateTime | BaseEntity 상속 |

## PostImage

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| imageUrl | String | |
| imageOrder | int | 이미지 순서 |
| post_id | Long (FK) | `@ManyToOne(LAZY)` → Post, NOT NULL |
| createdAt | LocalDateTime | BaseEntity 상속 |
| updatedAt | LocalDateTime | BaseEntity 상속 |

## Comment

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| content | String (max 1000) | |
| likeCount | int | 비정규화 |
| hidden | boolean | 관리자 숨김 |
| deleted | boolean | soft delete |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL |
| post_id | Long (FK) | `@ManyToOne(LAZY)` → Post, NOT NULL |
| parent_id | Long (FK) | `@ManyToOne(LAZY)` → Comment, nullable (자기참조, 2-depth) |
| children | List\<Comment\> | `@OneToMany(mappedBy="parent")` |
| createdAt | LocalDateTime | BaseEntity 상속 |
| updatedAt | LocalDateTime | BaseEntity 상속 |

## Category

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| name | String (unique) | |

> BaseEntity 미상속. createdAt/updatedAt 없음.

## PostLike

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL |
| post_id | Long (FK) | `@ManyToOne(LAZY)` → Post, NOT NULL |
| createdAt | LocalDateTime | 직접 관리 (`@Column`, 기본값 now) |

> `@UniqueConstraint(user_id, post_id)` · BaseEntity 미상속

## CommentLike

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL |
| comment_id | Long (FK) | `@ManyToOne(LAZY)` → Comment, NOT NULL |
| createdAt | LocalDateTime | 직접 관리 |

> `@UniqueConstraint(user_id, comment_id)` · BaseEntity 미상속

## Bookmark

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL |
| post_id | Long (FK) | `@ManyToOne(LAZY)` → Post, NOT NULL |
| createdAt | LocalDateTime | 직접 관리 |

> `@UniqueConstraint(user_id, post_id)` · BaseEntity 미상속

## ViewLog

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL |
| post_id | Long (FK) | `@ManyToOne(LAZY)` → Post, NOT NULL |
| viewedAt | LocalDateTime | 조회 시점 |

> `@UniqueConstraint(user_id, post_id)` · BaseEntity 미상속

## Notification

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| type | NotificationType (enum) | `COMMENT`, `REPLY`, `POST_LIKE`, `COMMENT_LIKE` |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL (알림 수신자) |
| postId | Long | 단순 Long (FK 아님). 관련 게시글 ID |
| commentId | Long | 단순 Long (FK 아님). nullable |
| actorId | Long | 단순 Long (FK 아님). 행위자 ID |
| actorNickname | String | 생성 시점 스냅샷 복사 (join 불필요) |
| actorProfileImg | String | 생성 시점 스냅샷 복사. nullable |
| message | String | 알림 메시지 |
| isRead | boolean | 읽음 여부 |
| createdAt | LocalDateTime | 직접 관리 |

> actorId·postId·commentId를 FK 없이 단순 Long으로 저장하여, 참조 대상이 삭제/탈퇴되어도 알림 데이터가 유지된다. BaseEntity 미상속.

## Report

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| reason | ReportReason (enum) | `SPAM`, `ABUSE`, `INAPPROPRIATE`, `FALSE_INFO`, `OTHER` |
| user_id | Long (FK) | `@ManyToOne(LAZY)` → User, NOT NULL (신고자) |
| post_id | Long (FK) | `@ManyToOne(LAZY)` → Post, nullable |
| comment_id | Long (FK) | `@ManyToOne(LAZY)` → Comment, nullable |
| createdAt | LocalDateTime | 직접 관리 |

> post_id와 comment_id 중 하나만 값이 있는 다형적 구조. `@UniqueConstraint(user_id, post_id)`, `@UniqueConstraint(user_id, comment_id)`. BaseEntity 미상속.

## RefreshToken

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| userId | Long | 유저 ID |
| token | String | Refresh Token 값 |
| expiryDate | LocalDateTime | 만료 시각 |

> 현재 MySQL 저장, Redis 전환 예정. 만료 토큰은 `@Scheduled`로 매일 새벽 3시 일괄 삭제.
