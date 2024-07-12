package com.jh.movieticket.theater.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상영관 생성 dto
public class TheaterCreateDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "상영관 이름을 입력해주세요.")
        @Pattern(regexp = "^[1-9][0-9]?관$", message = "올바른 상영관 이름을 입력해주세요.(ex. 1관, 10관)")
        private String name; // 상영관 이름

        @Positive(message = "총 좌석 수는 0 또는 음수일 수 없습니다.")
        private int seatCnt; // 총 좌석 수
    }
}
