package com.jh.movieticket.movie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PosterErrorCode {

    FAIL_DELETE_IMAGE(HttpStatus.BAD_REQUEST.value(), "이미지 삭제에 실패했습니다."),
    FAIL_CONVERT_TO_FILE(HttpStatus.BAD_REQUEST.value(), "Multipart 타입을 File 타입으로 변경하는데 실패했습니다.");

    private final int status;
    private final String message;
}
