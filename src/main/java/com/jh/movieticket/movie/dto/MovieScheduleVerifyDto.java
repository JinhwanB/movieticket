package com.jh.movieticket.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.jh.movieticket.theater.dto.TheaterVerifyDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 영화스케줄 조회 시 dto
public class MovieScheduleVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private MovieVerifyDto.Response movie; // 영화
        private TheaterVerifyDto.Response theater; // 상영관

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate; // 종영 날짜

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd TT:mm", timezone = "Asia/Seoul")
        private LocalDateTime startTime; // 시작 시간

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd TT:mm", timezone = "Asia/Seoul")
        private LocalDateTime endTime; // 끝나는 시간
    }
}
