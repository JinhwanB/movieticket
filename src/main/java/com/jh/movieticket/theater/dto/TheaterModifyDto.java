package com.jh.movieticket.theater.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상영관 수정용 dto
public class TheaterModifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request{

        @NotBlank(message = "변경을 원하는 상영관의 이름을 입력해주세요.")
        @Pattern(regexp = "^[1-9][0-9]?관$", message = "올바른 상영관 이름을 입력해주세요. (ex. 1관, 2관, 10관, 11관)")
        private String originName; // 원래 이름

        @NotBlank(message = "새로운 상영관의 이름을 입력해주세요.")
        @Pattern(regexp = "^[1-9][0-9]?관$", message = "올바른 상영관 이름을 입력해주세요. (ex. 1관, 2관, 10관, 11관)")
        private String changedName; // 수정하고자 하는 이름
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private String name; // 상영관 이름
        private int seatCnt; // 총 좌석 수
    }
}
