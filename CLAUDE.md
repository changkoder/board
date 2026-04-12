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
- 코드 수정 및 추가 시 반드시 다른 도메인의 코드 규칙과 방식을 참고해서 작성할 것

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

## 프론트-백엔드 계약

백엔드 수정 시 프론트엔드 영향 확인 필수, 프론트엔드 수정 시 백엔드 영향 확인 필수.
- API 응답 필드 추가/제거/이름변경 → 프론트에서 해당 필드 사용 여부 grep
- API 경로/메서드 변경 → 프론트 api/ 디렉토리에서 호출부 확인
- 에러코드/메시지 변경 → 프론트 에러 핸들링 확인
- 프론트에서 안 쓰이는 필드를 "나중에 쓸 수도"로 추가하지 않음

## 자주 하는 실수

- 파일 수정 후에는 반드시 테스트 해보기. 테스트 코드 전체 실행해보기

## graphify

이 프로젝트에는 `graphify-out/` 경로에 graphify 지식 그래프가 있다.

규칙:
- 아키텍처나 코드베이스 관련 질문에 답하기 전에 `graphify-out/GRAPH_REPORT.md`를 먼저 읽어 god node와 community 구조를 파악할 것
- `graphify-out/wiki/index.md`가 존재하면 원본 파일을 직접 읽지 말고 이 인덱스를 통해 탐색할 것
- 이 세션에서 코드 파일을 수정한 뒤에는 그래프를 최신 상태로 유지하기 위해 다음 명령을 실행할 것: `python3 -c "from graphify.watch import _rebuild_code; from pathlib import Path; _rebuild_code(Path('.'))"`
