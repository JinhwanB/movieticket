package com.jh.movieticket.reservation.dto;

import com.jh.movieticket.member.dto.MemberServiceDto;
import com.jh.movieticket.movie.dto.MovieScheduleServiceDto;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 서비스 레이어 dto
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class ReservationServiceDto implements Serializable {

    private Long id;
    private String reservationNumber; // 예약 번호
    private MemberServiceDto member; // 회원 아이디
    private MovieScheduleServiceDto movieSchedule; // 영화 스케줄
    private int seatNo; // 좌석 번호

    /**
     * ServiceDt0 -> CreateResponse
     *
     * @return CreateResponse
     */
    public ReservationCreateDto.Response toCreateResponse() {

        return ReservationCreateDto.Response.builder()
            .id(id)
            .reservationNumber(reservationNumber)
            .movieSchedule(movieSchedule)
            .seatNo(seatNo)
            .build();
    }

    /**
     * ServiceDto -> VerifyResponse
     *
     * @return VerifyResponse
     */
    public ReservationVerifyDto.Response toVerifyResponse() {

        return ReservationVerifyDto.Response.builder()
            .id(id)
            .reservationNumber(reservationNumber)
            .movieSchedule(movieSchedule.toVerifyResponse())
            .seatNo(seatNo)
            .build();
    }
}
