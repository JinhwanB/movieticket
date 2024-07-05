package com.jh.movieticket.theater.dto;

import com.jh.movieticket.theater.domain.Theater;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 서비스 레이어에서 사용할 dto
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class SeatServiceDto {

    private Long id;
    private int seatNo; // 좌석 번호
    private Theater theater; // 상영관
    private LocalDateTime registerDate; // 생성 날짜
    private LocalDateTime changeDate; // 수정 날짜
    private LocalDateTime deleteDate; // 삭제 날짜
}
