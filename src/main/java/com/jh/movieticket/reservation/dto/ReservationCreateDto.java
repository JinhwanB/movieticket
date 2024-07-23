package com.jh.movieticket.reservation.dto;

import com.jh.movieticket.movie.dto.MovieScheduleServiceDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 생성 시 dto
public class ReservationCreateDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "회원 아이디를 입력해주세요.")
        private String memberId; // 회원 아이디

        @NotNull(message = "pk값은 null일 수 없습니다.")
        @Positive(message = "pk값은 0 또는 음수일 수 없습니다.")
        private Long movieScheduleId; // 영화스케줄 pk

        @Positive(message = "좌석 번호는 0 또는 음수일 수 없습니다.")
        private int seatNo; // 좌석 번호
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String reservationNumber; // 예약 번호
        private MovieScheduleServiceDto movieSchedule; // 영화스케줄
        private int seatNo; // 좌석 번호
    }
}
