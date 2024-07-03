package com.jh.movieticket.mail.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailErrorCode {
    FAIL_SEND_EMAIL(HttpStatus.BAD_REQUEST.value(), "이메일 발송에 실패했습니다.");

    private final int status;
    private final String message;
}