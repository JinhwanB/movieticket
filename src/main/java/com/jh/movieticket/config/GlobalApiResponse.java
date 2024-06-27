package com.jh.movieticket.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 전체 api 응답 형식 통일화
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class GlobalApiResponse<T> {

    private int status; // HTTP 응답 코드
    private String message; // 응답 관련 메시지
    private T data; // 응답으로 받은 데이터

    // 응답 성공시
    public static <T> GlobalApiResponse<T> toGlobalResponse(T data){

        return GlobalApiResponse.<T>builder()
            .status(200)
            .message("성공")
            .data(data)
            .build();
    }

    // 응답 실패 시
    public static <T> GlobalApiResponse<T> toGlobalResponseFail(int status, String message){

        return GlobalApiResponse.<T>builder()
            .status(status)
            .message(message)
            .build();
    }
}