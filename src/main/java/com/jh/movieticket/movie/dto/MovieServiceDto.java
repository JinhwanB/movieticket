package com.jh.movieticket.movie.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.jh.movieticket.movie.domain.ScreenType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class MovieServiceDto implements Serializable { // 영화 서비스 레이어에서 사용할 dto

    private Long id; // pk
    private String posterName; // 포스터 이미지 파일 이름
    private String posterUrl; // 포스터 이미지 링크
    private String title; // 영화 제목
    private String director; // 감독
    private List<String> actorList; // 배우
    private List<String> genreList; // 장르
    private String description; // 영화에 대한 설명
    private String totalShowTime; // 총 상영 시간

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate releaseDate; // 개봉 날짜

    private double gradeAvg; // 평균 평점
    private double reservationRate; // 예매율
    private long totalAudienceCnt; // 누적 관객 수
    private ScreenType screenType; // 상영 타입

    /**
     * ServiceDto -> CreateResponse
     *
     * @return CreateResponse
     */
    public MovieCreateDto.Response toCreateResponse() {

        return MovieCreateDto.Response.builder()
            .posterUrl(posterUrl)
            .title(title)
            .director(director)
            .actorList(actorList)
            .genreList(genreList)
            .description(description)
            .totalShowTime(totalShowTime)
            .releaseYear(releaseDate.getYear())
            .releaseMonth(releaseDate.getMonthValue())
            .releaseDay(releaseDate.getDayOfMonth())
            .gradeAvg(gradeAvg)
            .reservationRate(reservationRate)
            .totalAudienceCnt(totalAudienceCnt)
            .screenType(screenType)
            .build();
    }

    /**
     * ServiceDto -> ModifyResponse
     *
     * @return ModifyResponse
     */
    public MovieModifyDto.Response toModifyResponse() {

        return MovieModifyDto.Response.builder()
            .posterUrl(posterUrl)
            .title(title)
            .director(director)
            .actorList(actorList)
            .genreList(genreList)
            .description(description)
            .totalShowTime(totalShowTime)
            .releaseYear(releaseDate.getYear())
            .releaseMonth(releaseDate.getMonthValue())
            .releaseDay(releaseDate.getDayOfMonth())
            .gradeAvg(gradeAvg)
            .reservationRate(reservationRate)
            .totalAudienceCnt(totalAudienceCnt)
            .screenType(screenType)
            .build();
    }

    /**
     * ServiceDto -> VerifyDto
     *
     * @return VerifyDto
     */
    public MovieVerifyDto.Response toVerifyResponse() {

        return MovieVerifyDto.Response.builder()
            .id(id)
            .posterUrl(posterUrl)
            .title(title)
            .director(director)
            .actorList(actorList)
            .genreList(genreList)
            .description(description)
            .totalShowTime(totalShowTime)
            .releaseYear(releaseDate.getYear())
            .releaseMonth(releaseDate.getMonthValue())
            .releaseDay(releaseDate.getDayOfMonth())
            .gradeAvg(gradeAvg)
            .reservationRate(reservationRate)
            .totalAudienceCnt(totalAudienceCnt)
            .screenType(screenType)
            .build();
    }

    /**
     * ServiceDto -> SearchDto
     *
     * @return SearchDto
     */
    public MovieSearchDto.Response toSearchResponse() {

        return MovieSearchDto.Response.builder()
            .id(id)
            .posterUrl(posterUrl)
            .title(title)
            .director(director)
            .actorList(actorList)
            .genreList(genreList)
            .description(description)
            .totalShowTime(totalShowTime)
            .releaseYear(releaseDate.getYear())
            .releaseMonth(releaseDate.getMonthValue())
            .releaseDay(releaseDate.getDayOfMonth())
            .gradeAvg(gradeAvg)
            .reservationRate(reservationRate)
            .totalAudienceCnt(totalAudienceCnt)
            .screenType(screenType)
            .build();
    }
}
