package com.jh.movieticket.movie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PosterErrorCode {

    FAIL_DELETE_IMAGE(HttpStatus.BAD_REQUEST.value(), "이미지 삭제에 실패했습니다."),
    FAIL_UPLOAD_IMAGE(HttpStatus.BAD_REQUEST.value(), "이미지를 s3에 업로드하는데 실패했습니다.");

    private final int status;
    private final String message;
}
