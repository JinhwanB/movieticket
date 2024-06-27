package com.jh.movieticket.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {
    ALREADY_LOGOUT(400, "이미 로그아웃된 유저입니다."),
    NOT_MATCH_CODE(400, "인증코드가 올바르게 입력되지 않았습니다."),
    NOT_MATCH_PASSWORD(400, "비밀번호가 다릅니다."),
    EXIST_USER_ID(400, "이미 사용중인 아이디입니다."),
    EXIST_EMAIL(400, "이메일로 등록된 회원이 있습니다."),
    NOT_FOUND_MEMBER(400, "회원으로 등록된 유저가 아닙니다.");

    private final int status;
    private final String message;
}
