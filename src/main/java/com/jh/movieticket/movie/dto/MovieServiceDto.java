package com.jh.movieticket.movie.dto;

import java.io.Serializable;
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

    private String posterUrl; // 포스터 이미지 링크
    private String title; // 영화 제목
    private String director; // 감독
    private List<String> actorList; // 배우
    private List<String> genreList; // 장르
    private String description; // 영화에 대한 설명
    private String totalShowTime; // 총 상영 시간
    private String releaseDate; // 개봉 날짜
    private double gradeAvg; // 평균 평점
    private double reservationRate; // 예매율
    private long totalAudienceCnt; // 누적 관객 수
    private String screenType; // 상영 타입
}
