package com.jh.movieticket.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 좌석 상태 enum 클래스
@Getter
@AllArgsConstructor
public enum SeatType {
    AVAILABLE("AVAILABLE"), // 예약 가능한 상태
    UNAVAILABLE("UNAVAILABLE"), // 사용 불가
    BOOKED("BOOKED"); // 예약 완료된 상태

    private final String name;
}
