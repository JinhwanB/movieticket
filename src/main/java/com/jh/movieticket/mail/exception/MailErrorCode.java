package com.jh.movieticket.mail.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailErrorCode {
    FAIL_SEND_EMAIL(400, "이메일 발송에 실패했습니다.");

    private final int status;
    private final String message;
}