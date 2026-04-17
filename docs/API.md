← [README로 돌아가기](../README.md)

# API Endpoints

## Auth (4개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 | X |
| POST | `/api/auth/logout` | 로그아웃 | O |
| POST | `/api/auth/refresh` | 토큰 재발급 | X |

## User (11개)

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

## Post (8개)

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

## Comment (4개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/posts/{postId}/comments` | 댓글 목록 | X |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | O |
| PATCH | `/api/comments/{commentId}` | 댓글 수정 | O |
| DELETE | `/api/comments/{commentId}` | 댓글 삭제 | O |

## Like / Bookmark / Report (5개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/posts/{postId}/like` | 게시글 좋아요 토글 | O |
| POST | `/api/comments/{commentId}/like` | 댓글 좋아요 토글 | O |
| POST | `/api/posts/{postId}/bookmark` | 북마크 토글 | O |
| POST | `/api/posts/{postId}/report` | 게시글 신고 | O |
| POST | `/api/comments/{commentId}/report` | 댓글 신고 | O |

## Image (1개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/images` | 이미지 업로드 (다중) | O |

## Notification (4개)

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/notifications` | 알림 목록 | O |
| GET | `/api/notifications/unread-count` | 미읽음 알림 수 | O |
| PATCH | `/api/notifications/{id}/read` | 알림 읽음 처리 | O |
| PATCH | `/api/notifications/read-all` | 전체 읽음 처리 | O |

## Admin (13개)

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
