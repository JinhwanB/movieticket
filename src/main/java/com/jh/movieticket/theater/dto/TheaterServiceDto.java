package com.jh.movieticket.theater.dto;

import java.io.Serializable;
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
public class TheaterServiceDto implements Serializable {

    private Long id;
    private String name; // 상영관 이름
    private int seatCnt; // 총 좌석 수
    private LocalDateTime registerDate; // 생성 날짜
    private LocalDateTime changeDate; // 수정 날짜
    private LocalDateTime deleteDate; // 삭제 날짜
}
