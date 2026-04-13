# Board — 게시판 웹 애플리케이션

Spring Boot 기반의 커뮤니티 게시판 프로젝트입니다.  
JWT 인증, Querydsl 동적 쿼리, 관리자 기능, 알림 시스템 등 실무에서 요구되는 백엔드 핵심 기능을 직접 설계하고 구현했습니다.

## 주요 기능

- 게시글 CRUD, 검색(제목/내용/작성자), 인기글, 무한스크롤
- 댓글/대댓글 (2-depth 계층 구조), @멘션 알림
- 좋아요 토글 (게시글/댓글), 북마크
- 이미지 다중 업로드 (에디터 미리보기 지원)
- 알림 시스템 (댓글, 좋아요, 답글, 멘션) + 중복 방지
- 신고 누적 → 자동 숨김 (5회 임계값)
- 관리자 기능 (숨김/복원/강제삭제, 유저 차단, 신고 내역 조회)
- JWT 인증 (Access + Refresh Token Rotation)

---

## 기술 스택

| 구분 | 기술 |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.3.7 |
| **Security** | Spring Security, JWT (jjwt 0.12.6) |
| **ORM** | Spring Data JPA, Querydsl 5.1.0 |
| **Database** | MySQL (운영), H2 (테스트) |
| **Frontend** | React (Vite) — 별도 프로젝트로 분리 |
| **Build** | Gradle |
| **기타** | P6Spy (SQL 로깅), Lombok |

---

## ERD

> 엔티티 12개 · 모든 `@ManyToOne`은 `LAZY` · Soft delete 적용 엔티티: User, Post, Comment

```
 ┌─────────────┐         ┌─────────────────┐         ┌────────────┐
 │  Category   │ 1     N │      Post       │ N     1 │    User    │
 │─────────────│────────▶│─────────────────│◀────────│────────────│
 │ id          │         │ id              │         │ id         │
 │ name        │         │ title, content  │         │ email      │
 └─────────────┘         │ viewCount       │         │ password   │
                         │ likeCount       │         │ nickname   │
                         │ commentCount    │         │ profileImg │
                         │ hidden, deleted │         │ role       │
                         └──┬──────────┬───┘         │ status     │
                            │          │              │ postCount  │
                      1:N ──┘          └── 1:N        │ deleted    │
                 ┌──────────┐    ┌──────────────┐     └─────┬──────┘
                 │PostImage │    │   Comment    │           │
                 │──────────│    │──────────────│     1:N ──┘
                 │ imageUrl │    │ content      │◀──────────┘
                 │ imgOrder │    │ likeCount    │
                 └──────────┘    │ hidden       │
                                 │ deleted      │
                                 │ parent_id ──▶│ 자기참조 (2-depth 대댓글)
                                 └──────────────┘
```

### 행위 기록 엔티티

> User ↔ Post 또는 Comment 사이의 행위를 기록한다. 모두 `@UniqueConstraint`로 중복 방지.

```
 ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
 │   PostLike   │  │ CommentLike  │  │   Bookmark   │  │   ViewLog    │
 │──────────────│  │──────────────│  │──────────────│  │──────────────│
 │ user_id (FK) │  │ user_id (FK) │  │ user_id (FK) │  │ user_id (FK) │
 │ post_id (FK) │  │ comment_id   │  │ post_id (FK) │  │ post_id (FK) │
 │ createdAt    │  │ createdAt    │  │ createdAt    │  │ viewedAt     │
 └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
  UK(user, post)    UK(user, comment)  UK(user, post)    UK(user, post)
```

### Notification · Report · RefreshToken

```
 ┌─────────────────────────┐    ┌────────────────────────┐    ┌────────────────┐
 │      Notification       │    │        Report          │    │  RefreshToken  │
 │─────────────────────────│    │────────────────────────│    │────────────────│
 │ user_id (FK, 수신자)    │    │ user_id (FK, 신고자)   │    │ userId         │
 │ type (COMMENT/REPLY/    │    │ post_id (FK, nullable) │    │ token          │
 │   POST_LIKE/COMMENT_LIKE│    │ comment_id (nullable)  │    │ expiryDate     │
 │ postId (Long)           │    │ reason (enum)          │    └────────────────┘
 │ commentId (Long)        │    │ createdAt              │
 │ actorId (Long)          │    └────────────────────────┘
 │ actorNickname ──────────│─ 스냅샷 저장 (생성 시점 복사)
 │ actorProfileImg         │
 │ message, isRead         │   * actorId, postId, commentId는
 │ createdAt               │     FK 없이 단순 Long으로 저장
 └─────────────────────────┘     → 참조 대상 삭제 시에도 알림 유지
```

---

## API 목록

### Auth (4개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 | X |
| POST | `/api/auth/logout` | 로그아웃 | O |
| POST | `/api/auth/refresh` | 토큰 재발급 | X |

### User (11개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/users/me` | 내 정보 조회 | O |
| PATCH | `/api/users/me` | 내 정보 수정 | O |
| PATCH | `/api/users/me/password` | 비밀번호 변경 | O |
| DELETE | `/api/users/me` | 회원 탈퇴 | O |
| GET | `/api/users/me/posts` | 내가 쓴 게시글 | O |
| GET | `/api/users/me/comments` | 내가 쓴 댓글 | O |
| GET | `/api/users/me/likes` | 좋아요한 게시글 | O |
| GET | `/api/users/me/bookmarks` | 북마크한 게시글 | O |
| GET | `/api/users/{userId}` | 유저 프로필 조회 | X |
| GET | `/api/users/nickname/{nickname}` | 닉네임으로 프로필 조회 | O |
| GET | `/api/users/{userId}/posts` | 유저의 게시글 | X |

### Post (8개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/posts` | 게시글 목록 (카테고리 필터, 페이징) | X |
| GET | `/api/posts/infinite` | 무한스크롤 목록 (no-offset) | X |
| GET | `/api/posts/search` | 게시글 검색 | X |
| GET | `/api/posts/popular` | 인기 게시글 | X |
| GET | `/api/posts/{postId}` | 게시글 상세 | X |
| POST | `/api/posts` | 게시글 작성 | O |
| PATCH | `/api/posts/{postId}` | 게시글 수정 | O |
| DELETE | `/api/posts/{postId}` | 게시글 삭제 | O |

### Comment (4개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/posts/{postId}/comments` | 댓글 목록 | X |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | O |
| PATCH | `/api/comments/{commentId}` | 댓글 수정 | O |
| DELETE | `/api/comments/{commentId}` | 댓글 삭제 | O |

### Like / Bookmark / Report (5개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/posts/{postId}/like` | 게시글 좋아요 토글 | O |
| POST | `/api/comments/{commentId}/like` | 댓글 좋아요 토글 | O |
| POST | `/api/posts/{postId}/bookmark` | 북마크 토글 | O |
| POST | `/api/posts/{postId}/report` | 게시글 신고 | O |
| POST | `/api/comments/{commentId}/report` | 댓글 신고 | O |

### Image (1개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/images` | 이미지 업로드 (다중) | O |

### Notification (4개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/notifications` | 알림 목록 | O |
| GET | `/api/notifications/unread-count` | 미읽음 알림 수 | O |
| PATCH | `/api/notifications/{id}/read` | 알림 읽음 처리 | O |
| PATCH | `/api/notifications/read-all` | 전체 읽음 처리 | O |

### Admin (13개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/admin/posts/hidden` | 숨김 게시글 목록 | ADMIN |
| GET | `/api/admin/comments/hidden` | 숨김 댓글 목록 | ADMIN |
| GET | `/api/admin/posts/{postId}` | 게시글 상세 (신고 내역 포함) | ADMIN |
| GET | `/api/admin/comments/{commentId}` | 댓글 상세 (신고 내역 포함) | ADMIN |
| PATCH | `/api/admin/posts/{postId}/hide` | 게시글 숨김 | ADMIN |
| PATCH | `/api/admin/comments/{commentId}/hide` | 댓글 숨김 | ADMIN |
| PATCH | `/api/admin/posts/{postId}/restore` | 게시글 숨김 해제 | ADMIN |
| PATCH | `/api/admin/comments/{commentId}/restore` | 댓글 숨김 해제 | ADMIN |
| DELETE | `/api/admin/posts/{postId}` | 게시글 강제 삭제 | ADMIN |
| DELETE | `/api/admin/comments/{commentId}` | 댓글 강제 삭제 | ADMIN |
| GET | `/api/admin/users/blocked` | 차단 유저 목록 | ADMIN |
| POST | `/api/admin/users/{userId}/block` | 유저 차단 | ADMIN |
| DELETE | `/api/admin/users/{userId}/block` | 유저 차단 해제 | ADMIN |

> 총 50개 엔드포인트 (인증 불필요 14개 / 로그인 필요 23개 / 관리자 전용 13개)

---

## 기술적 의사결정

### 1. JWT 인증 구조

**Access Token(30분) + Refresh Token(7일)** 으로 분리하고, 재발급 시 Refresh Token도 함께 교체하는 **Rotation 방식**을 적용했다. 탈취된 Refresh Token이 재사용되는 것을 방지하기 위함이다.

- JWT에는 최소 정보(userId, role)만 담는다. 변경 가능한 정보(닉네임 등)를 넣으면 토큰 재발급 전까지 불일치가 발생하고, 디코딩 시 노출 위험이 있다.
- Access Token 검증은 **필터 레벨**(JwtAuthenticationFilter), Refresh Token 검증은 **서비스 레벨**(AuthService)에서 처리한다. Access Token은 매 요청마다 자동 검증이 필요하고, Refresh Token은 재발급 요청 시에만 검증하면 되기 때문이다.
- `/api/auth/refresh`는 인증 없이(permitAll) 호출 가능하다. 이 API를 호출하는 시점에 Access Token은 이미 만료된 상태이기 때문이다.
- Refresh Token은 현재 MySQL에 저장하며, 만료 토큰은 `@Scheduled`로 매일 새벽 3시에 일괄 삭제한다. 토큰은 휘발성 데이터이므로 Redis TTL로 전환하면 스케줄러 없이 자동 만료가 가능하다.
- CSRF는 비활성화했다. JWT 방식은 쿠키가 아닌 Authorization 헤더로 토큰을 전송하므로 CSRF 공격이 성립하지 않는다.

### 2. Querydsl 전체 통일

전체 Repository의 커스텀 쿼리를 **Querydsl로 통일**했다. 

- 컴파일 타임에 Q클래스 기반 타입 체크가 가능하여, 필드명 오타나 타입 불일치를 런타임이 아닌 컴파일 시점에 잡을 수 있다.
- `BooleanExpression` 조합으로 검색 조건(제목/내용/작성자/카테고리)에 따른 **동적 쿼리**를 깔끔하게 구성할 수 있다. 조건이 null이면 해당 where절이 자동으로 무시된다.
- 목록/상세 조회에는 **fetchJoin**을 적용하여 N+1 문제를 방지했다. 모든 목록 쿼리에서 `post.user`와 `post.category`를 한 번에 조회한다.

### 3. 비정규화 (카운트 필드)

Post 엔티티에 viewCount, likeCount, commentCount를 직접 보유한다. 목록 조회 시 매번 COUNT 서브쿼리를 실행하는 대신, 좋아요/댓글 생성·삭제 시점에 카운트를 증감하는 방식이다.

- **적용 기준**: 화면에 노출되는 숫자 + 조회 빈도가 높은 경우에만 비정규화한다. bookmarkCount는 UI에 노출하지 않으므로 비정규화하지 않았다.
- 쓰기 비용이 소폭 증가하지만(좋아요/댓글 CUD 시 UPDATE 1회 추가), 읽기 비용이 대폭 감소한다(목록 조회 시 JOIN/COUNT 제거).

### 4. 소프트 딜리트 + hidden 분리

`deleted`(유저 삭제)와 `hidden`(관리자 숨김)을 **별도 boolean 필드로 분리** 관리한다.

- 유저가 삭제한 글은 복원 불가하지만, 관리자가 숨긴 글은 복원할 수 있어야 한다. 하나의 필드로 관리하면 이 두 가지 상태를 구분할 수 없다.
- 소프트 딜리트를 적용하는 이유는 데이터 정합성 유지와 신고 이력 보존 때문이다. 탈퇴한 유저의 게시글/댓글이 물리 삭제되면 다른 유저의 데이터와 참조 관계가 깨진다.
- 탈퇴 유저의 닉네임은 "(탈퇴한 사용자)"로 표시한다. 모든 Response DTO의 `from()` 메서드에서 일관되게 처리한다.
- deleted/hidden 체크 위치는 **한 곳에서만**: 커스텀 Querydsl 메서드를 탈 때는 쿼리 WHERE 절에서, JPA 기본 `findById`를 탈 때는 Service에서 검증한다. 이중 체크를 피하되 누락도 없도록 전 메서드를 교차 검증했다.

### 5. 전역 예외 처리

ErrorCode enum(28개)에 HTTP 상태 코드와 메시지를 도메인별로 정의하고, `CustomException`으로 던진다.

- `@RestControllerAdvice` GlobalExceptionHandler가 이를 잡아 `ApiResponse.error(message)` 형태의 **통일된 JSON**으로 반환한다.
- Bean Validation 실패(`MethodArgumentNotValidException`)도 동일한 포맷으로 처리한다.
- 인증 실패(401)는 `CustomAuthenticationEntryPoint`에서, 인가 실패(403)는 `accessDeniedHandler`에서 각각 JSON 형태로 응답한다. Spring Security 기본 동작(HTML 리다이렉트)을 REST API에 맞게 커스텀했다.

### 6. API 응답 통일과 DTO 설계

**`ApiResponse<T>`** 의 정적 팩토리 메서드(`success()`, `error()`)로 `{ success, data, message }` 포맷을 통일했다. 생성자를 private으로 감추어 유효하지 않은 상태의 응답 객체가 생성되는 것을 방지한다.

- **본인/타인 DTO 분리**: `UserResponse`(본인용: email, role 포함)와 `UserProfileResponse`(타인용: nickname, profileImg만)를 분리하여 민감 정보를 보호한다.
- **관리자/일반 DTO 분리**: `AdminPostDetailResponse`(reports 필드 포함)와 `PostResponse`를 분리하여 관리자 전용 필드가 일반 응답에 섞이지 않도록 한다.
- **DTO 변환은 DTO 안에서**: 모든 Response DTO에 `from()` 정적 팩토리 메서드를 두어, 엔티티 → DTO 변환 로직을 DTO 내부에서 처리한다.

### 7. 인가 두 레벨 분리

인가를 **URL 레벨**과 **비즈니스 레벨**로 분리한다.

- **URL 레벨**(SecurityConfig): `/api/admin/**` 경로를 `hasRole("ADMIN")`으로 차단한다. Controller에서 별도 권한 체크가 불필요하다.
- **비즈니스 레벨**(Service): "본인 글인지", "공지글은 ADMIN만 작성 가능한지" 등의 소유권/비즈니스 규칙을 검증한다.
- 공지 작성 권한은 **이중 방어**: 프론트에서 UI를 숨기고, 백엔드 Service에서도 ADMIN 체크를 한다. API 직접 호출로 우회하는 것을 방지하기 위함이다.

### 8. N+1 방지 전략

**fetchJoin, LAZY 로딩, IN 쿼리**를 상황에 맞게 사용한다.

- **@ManyToOne (user, category)**: 목록/상세 조회 시 fetchJoin으로 한 방 조회. 모든 목록 쿼리에 일관 적용.
- **@OneToMany (images)**: fetchJoin하면 1:N 관계에서 row가 곱셈되어 `fetchOne()` 호출 시 `NonUniqueResultException`이 발생한다. LAZY 로딩으로 별도 쿼리 처리하고, user/category만 fetchJoin하는 전략을 선택했다.
- **댓글 좋아요 여부**: 댓글 ID 목록을 **IN 쿼리**로 한 번에 조회하고 `HashSet`에 담아, `contains()` O(1)로 판단한다. List로 반환하면 O(n)이므로 Set으로 변환하여 성능을 확보했다.
- **관리자 신고 내역**: 신고자 User를 `report.user` fetchJoin으로 1회에 조회. 상세 조회 쿼리 수를 2개로 고정했다.

### 9. 댓글 2-depth 계층 구조

Comment 엔티티가 **자기 참조**(`parent`)로 대댓글을 구현한다.

- 대대댓글(3-depth) 시도 시 Service에서 `parent.getParent() != null`이면 **최상위 부모로 재지정**하여 depth를 2로 제한한다.
- **정렬**: Querydsl CaseBuilder로 groupId(원댓글은 자기 id, 대댓글은 parent.id)를 계산하는 임시 값을 만들어, 별도 DB 컬럼 없이 부모-자식 순서를 보장한다. 3단계 정렬: 그룹 → 원댓글 먼저 → 시간순.
- DB에서 flat하게 가져온 뒤 자바에서 `Collectors.groupingBy()`로 계층 구조를 조립한다.
- **연관관계 편의 메서드를 의도적으로 사용하지 않았다.** 같은 트랜잭션에서 `children`을 조회하는 코드가 없어서 객체 불일치 문제가 발생하지 않고, 오히려 편의 메서드 추가 시 `getChildren()` 호출로 불필요한 LAZY 로딩 SELECT가 발생한다.
- **@멘션**: `@(\\S+)` 정규식으로 댓글 내용에서 닉네임을 추출하고, `LinkedHashSet`으로 중복 멘션을 제거한 뒤 해당 유저에게 알림을 전송한다.

### 10. 좋아요 토글 + 데이터 정합성

좋아요/취소 API를 분리하지 않고 **하나의 API로 토글** 처리한다. 존재하면 삭제(return false), 없으면 생성(return true), 비정규화 카운트도 동기화한다.

- **`@UniqueConstraint`로 이중 방어**: 서비스 로직에서 exists 체크를 하지만, 동시 요청 시 race condition이 발생할 수 있다. DB 레벨 유니크 제약으로 데이터 정합성을 최종 보장한다.
- **exists vs find 분리**: 확인만 할 때는 `existsByUserAndPost`(SELECT 1로 빠름), 객체가 필요할 때는 `findByUserAndPost`로 용도에 따라 분리한다.
- **좋아요 취소 시 알림 레코드 유지**: 좋아요 취소 시 삭제되는 것은 PostLike/CommentLike 레코드만이고, Notification 레코드는 DB에 남는다. 두 테이블을 별개로 관리하여 연타 시에도 첫 1회만 알림이 발송된다.

### 11. 알림 설계 — 스냅샷 저장 + 성능 최적화

Notification에 actorNickname, actorProfileImg를 **생성 시점에 복사**하여 저장한다.

- **@ManyToOne vs 단순 Long ID 설계 기준**: receiver(user)는 알림 목록 조회 시 객체 탐색이 필요하므로 `@ManyToOne`. actorId/postId/commentId는 ID 비교만 하면 되므로 단순 Long. FK 제약이 없어서 참조 대상이 탈퇴/삭제되어도 알림 데이터가 유지된다.
- **값 복사 저장의 조건**: "생성 시점의 데이터가 그대로 유지돼도 괜찮은 도메인"에만 적용한다. 알림은 "그 시점의 기록"이므로 적합하지만, 북마크/좋아요 목록은 게시글 제목/작성자의 최신 데이터를 보여줘야 하므로 fetch join이 필수다.
- **조회 성능**: notification 테이블 하나만 SELECT하면 응답에 필요한 모든 필드가 있다. 다른 테이블 join이 불필요하여 쿼리가 단순하다.
- **알림 중복 방지**: `existsLikeNotification`으로 동일 sender + post + type 조합이면 발송하지 않는다. 본인 행위(본인 글에 좋아요)에는 알림을 보내지 않는다.
- **전체 읽음 처리**: Querydsl 벌크 UPDATE(`queryFactory.update().set().where().execute()`)로 단일 쿼리 처리. 하나씩 조회해서 `read()` 호출하는 방식 대비 쿼리 수 N → 1.
- **`/unread-count` 별도 엔드포인트**: 페이징 도입으로 프론트에서 전체 미읽음 수를 계산할 수 없게 되어, 서버에서 COUNT 쿼리로 정확한 숫자를 내려주는 전용 API를 분리했다.

### 12. 이미지 업로드 분리

게시글 저장 API와 이미지 업로드 API를 **별도 API 2개로 분리**했다.

- **흐름**: 이미지 업로드(POST /api/images) → URL 응답 → 에디터 미리보기 → 게시글 저장 시 URL 문자열만 전달
- **분리 이유**: JSON(게시글 데이터)과 바이너리(이미지 파일)를 하나의 요청에 혼합하면 `multipart/form-data`와 `application/json`을 동시에 처리해야 하는 복잡도가 발생한다. 분리하면 에디터 미리보기도 자연스럽게 구현된다.
- **파일 검증 이중 체크**: contentType(`image/`로 시작하는지) + 확장자(허용 목록) 둘 다 검증한다. 한쪽만 체크하면 우회가 가능하다.
- **UUID 파일명**: 충돌 확률이 사실상 0이고, 원본 파일명 노출을 방지한다.
- **이미지 수정 전략**: 개별 diff 대신 `clearImages()` 후 전체 재등록 방식. `cascade = ALL + orphanRemoval = true`로 Post 중심의 이미지 생명주기를 관리한다.
- **한계점**: 게시글 작성 취소 시 고아 파일이 발생한다. 실무에서는 스케줄러로 미연결 파일을 정리하거나 임시 저장소를 활용한다.

### 13. 신고 자동숨김 + 이중 방어

게시글/댓글 신고가 **5회 누적**되면 자동으로 `hidden = true`로 변경한다.

- 임계값은 서비스 클래스의 상수(`HIDE_THRESHOLD`)로 관리하여 변경이 용이하다.
- **중복 신고 이중 방어**: 서비스 로직(`existsByUserAndPost`) + DB `@UniqueConstraint`. 동시 요청에도 데이터 정합성을 보장한다.
- 신고 사유는 `ReportReason` enum으로 관리하고, `@Enumerated(EnumType.STRING)`으로 DB에 문자열로 저장한다. ORDINAL은 enum 순서 변경 시 데이터가 깨지므로 사용하지 않는다.
- `ReportSummaryResponse`의 `toLabel()`에서 switch expression으로 enum → 한글 변환. 새 enum 값을 추가하면 컴파일 에러가 발생하여 누락을 방지한다.

### 14. 관리자 상세조회 분리

관리자용 상세조회 메서드(`findByIdWithDetailsIncludingHidden`)를 별도로 구현했다.

- **문제**: 숨김 목록에서 제목만 보이고, 상세 조회 시 기존 `findByIdWithDetails`의 `hidden.eq(false)` 조건 때문에 404가 발생했다.
- **해결**: 기존 일반 유저용 메서드는 수정하지 않고 관리자 전용 메서드를 **분리**하여, 일반 API에 숨김 데이터가 노출되는 사고를 원천 차단했다.
- DTO도 `PostResponse`(일반)와 `AdminPostDetailResponse`(관리자, reports 포함)로 네임스페이스를 분리한다.

### 15. 페이징 전략

**오프셋 페이징**(일반 목록)과 **no-offset 커서 기반 페이징**(무한스크롤)을 병행한다.

- 오프셋 페이징은 뒤로 갈수록 OFFSET만큼 row를 스캔하므로 느려진다. no-offset은 `WHERE id < ?`로 PK 인덱스를 타서 대량 데이터에서도 속도가 일정하다.
- `PageableExecutionUtils.getPage()`로 count 쿼리를 지연 실행하여, 데이터가 페이지 크기보다 적을 때 불필요한 COUNT 쿼리를 생략한다.
- 공지사항은 별도 쿼리로 조회한 뒤 일반 글과 합친다. 1페이지에서만 공지를 포함하고, 일반 글 쿼리에서는 공지를 제외하여 중복과 페이징 꼬임을 방지한다.

### 16. 엔티티 설계 원칙

- **Setter 금지**: `@Builder`를 생성자에 적용하여 빌더로 세팅 가능한 필드를 제한하고, 상태 변경은 도메인 메서드(`delete()`, `hide()`, `increaseLikeCount()` 등)를 통해서만 가능하다.
- **BaseEntity 선택적 상속**: User, Post, PostImage, Comment처럼 수정이 발생하는 엔티티만 BaseEntity(createdAt/updatedAt)를 상속한다. PostLike, Bookmark, ViewLog처럼 수정이 없는 도메인은 createdAt만 직접 관리하여 불필요한 updatedAt을 제거했다.
- **`@Enumerated(EnumType.STRING)`**: enum을 DB에 문자열로 저장한다. ORDINAL(숫자)은 enum 순서 변경 시 기존 데이터가 깨진다.
- **ViewLog 기반 조회수 중복 방지**: 유저당 게시글 1회만 조회수가 증가하며, 비로그인 사용자는 조회수를 올리지 않는 정책을 적용했다.

---

## 테스트

### 전략

**`@SpringBootTest` + H2 인메모리 DB**를 사용한 통합 테스트를 수행한다.

- **Mockito 대신 통합 테스트를 선택한 이유**: Repository를 전부 Querydsl로 직접 구현했기 때문에, WHERE 조건 누락이나 fetch join 오류 같은 **쿼리 자체의 정확성**까지 검증해야 한다. Mockito는 Repository 반환값을 개발자가 직접 지정하므로 쿼리 버그를 잡지 못한다.
- `@Transactional`로 매 테스트 후 자동 롤백하여 **테스트 간 데이터 격리**를 보장한다.
- 벌크 UPDATE 테스트(markAllAsRead 등)에서는 `em.flush()` + `em.clear()`로 1차 캐시를 비운 뒤 재조회하여 DB 반영 여부를 검증한다.
- Controller 레이어 테스트(MockMvc)는 Postman으로 대체했다.

### 주요 검증 항목

- JWT 토큰 만료 검증 (동일 비밀키로 순수 만료만 테스트)
- Refresh Token Rotation 동작 (Thread.sleep으로 시간 경과 시뮬레이션)
- 대대댓글 → 2-depth 유지 검증
- 댓글 좋아요 `liked` 필드가 부모/자식 댓글에 각각 정확히 반영되는지
- ViewLog 중복 조회수 미증가
- 신고 5회 누적 → 자동 숨김 동작
- 관리자 숨김 게시글 조회 가능 여부
- 이미지 파일 검증 (MockMultipartFile로 contentType/확장자 테스트)

### 현황

| 테스트 파일 | 메서드 수 |
|---|---|
| AdminServiceTest | 20 |
| UserServiceTest | 14 |
| PostServiceTest | 13 |
| CommentServiceTest | 13 |
| AuthServiceTest | 11 |
| PostRepositoryTest | 9 |
| NotificationServiceTest | 8 |
| ReportServiceTest | 7 |
| JwtTokenProviderTest | 6 |
| ImageServiceTest | 4 |
| LikeServiceTest | 4 |
| CommentLikeRepositoryTest | 4 |
| BookmarkServiceTest | 2 |
| ViewLogRepositoryTest | 2 |
| CommentRepositoryTest | 2 |

> 총 15개 파일, 119개 테스트 메서드

---

## 프로젝트 구조

```
com.project.board
├── domain
│   ├── admin/           controller, service
│   ├── bookmark/        controller, entity, repository, service
│   ├── category/        entity, repository
│   ├── comment/         controller, dto, entity, repository, service
│   ├── image/           controller, dto, service
│   ├── like/            controller, entity, repository, service
│   ├── notification/    controller, dto, entity, repository, service
│   ├── post/            controller, dto, entity, repository, service
│   ├── report/          controller, dto, entity, repository, service
│   ├── user/            controller, dto, entity, repository, service
│   └── viewlog/         entity, repository
└── global
    ├── common/          ApiResponse, BaseEntity
    ├── config/          SecurityConfig, JpaConfig, QuerydslConfig, WebConfig
    ├── exception/       ErrorCode, CustomException, GlobalExceptionHandler
    └── security/jwt/    JwtTokenProvider, JwtAuthenticationFilter, RefreshToken
```
