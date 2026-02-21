package com.project.board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    //인증
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    //유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호가 기존 비밀번호와 동일합니다."),

    //게시글
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

    //댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    //알림
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),

    //신고
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "이미 신고한 게시글/댓글입니다."),
    CANNOT_REPORT_OWN(HttpStatus.BAD_REQUEST, "본인의 글/댓글은 신고할 수 없습니다."),
    ALREADY_HIDDEN(HttpStatus.BAD_REQUEST, "이미 숨김 처리된 게시글/댓글입니다."),

    // 관리자
    ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "이미 차단된 회원입니다."),
    NOT_BLOCKED(HttpStatus.BAD_REQUEST, "차단되지 않은 회원입니다."),
    CANNOT_BLOCK_ADMIN(HttpStatus.BAD_REQUEST, "관리자는 차단할 수 없습니다."),
    CANNOT_DELETE_ADMIN(HttpStatus.BAD_REQUEST, "관리자 계정은 탈퇴할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
