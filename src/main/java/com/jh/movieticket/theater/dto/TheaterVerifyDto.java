package com.jh.movieticket.theater.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상영관 조회용 dto
public class TheaterVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response{

        private Long id; // pk
        private String name; // 상영관 이름
        private int seatCnt; // 총 좌석 수
    }
}
