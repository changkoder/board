# CLAUDE.md

## 프로젝트

게시판 웹앱. Java 17 / Spring Boot 3.4.x / Spring Security + JWT / JPA + QueryDSL / MySQL

## 패키지 구조

`com.project.board.domain.{도메인}/` 하위에 controller, service, dto, entity, repository 분리.
도메인: admin, bookmark, category, comment, image, like, notification, post, report, user, viewlog
글로벌: `global/common` (ApiResponse, BaseEntity), `global/exception`, `global/security/jwt`, `global/config`

## 지켜야 할 규칙

- 엔티티에 Setter 금지 — Builder + 도메인 메서드 사용
- 컨트롤러에 비즈니스 로직 금지 — 서비스에 위임
- 응답은 반드시 `ApiResponse<T>` 래핑
- 서비스 클래스에 `@Transactional(readOnly = true)` 기본, 변경 메서드만 `@Transactional`
- Repository 커스텀 쿼리는 QueryDSL (Custom 인터페이스 + Impl 클래스)
- N+1 방지: 목록/상세 조회 시 fetch join 필수
- Soft delete (deleted 필드) — 물리 삭제 안 함
- DTO 변환은 DTO의 `from()` 정적 메서드 또는 생성자에서
- 예외는 `throw new CustomException(ErrorCode.XXX)` — ErrorCode enum에 정의
- 인증 필요 엔드포인트는 `@AuthenticationPrincipal Long userId`

## 네이밍

Controller / Service / Repository+Custom+Impl / XxxRequest / XxxResponse / 엔티티는 단수형

## 주요 비즈니스 규칙

- 대댓글 depth 2 제한 (대대댓글 → 부모의 부모로 연결)
- 신고 5회 누적 → 자동 숨김
- 공지글은 ADMIN만 작성 가능 (categoryId: 1)
- 조회수는 로그인 유저당 1회만 (ViewLog)
- 좋아요 알림 중복 방지 / 본인 행위 알림 안 감
- 탈퇴 유저 닉네임 → "(탈퇴한 사용자)"
- 관리자 계정 탈퇴 불가

## 자주 하는 실수

- 파일 수정 후에는 반드시 테스트 해보기. 테스트 코드 전체 실행해보기
