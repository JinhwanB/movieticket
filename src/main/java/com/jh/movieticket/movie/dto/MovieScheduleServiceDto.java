package com.jh.movieticket.movie.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 영화스케줄 서비스 레이어 dto
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class MovieScheduleServiceDto implements Serializable {

    private Long id;
    private MovieServiceDto movie; // 영화
    private TheaterServiceDto theater; // 상영관

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate; // 종영 날짜

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime; // 시작 시간

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime; // 끝나는 시간

    /**
     * ServiceDto -> VerifyResponse
     *
     * @return VerifyResponse
     */
    public MovieScheduleVerifyDto.Response toVerifyResponse() {

        return MovieScheduleVerifyDto.Response.builder()
            .movie(movie.toVerifyResponse())
            .id(id)
            .endDate(endDate)
            .endTime(endTime)
            .startTime(startTime)
            .theater(theater.toVerifyResponse())
            .build();
    }
}
