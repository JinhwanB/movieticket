package com.jh.movieticket.reservation.dto;

import com.jh.movieticket.movie.dto.MovieScheduleVerifyDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 조회 시 dto
public class ReservationVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String reservationNumber; // 예약 번호
        private MovieScheduleVerifyDto.Response movieSchedule; // 영화스케줄
        private int seatNo; // 좌석 번호
    }
}
