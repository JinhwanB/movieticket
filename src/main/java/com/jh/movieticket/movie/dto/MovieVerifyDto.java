package com.jh.movieticket.movie.dto;

import com.jh.movieticket.movie.domain.ScreenType;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 영화 조회 시 dto
public class MovieVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id; // pk
        private String posterUrl; // 영화 포스터 링크
        private String title; // 영화 제목
        private String director; // 감독
        private List<String> actorList; // 배우
        private List<String> genreList; // 장르
        private String description; // 영화에 대한 설명
        private String totalShowTime; // 총 상영 시간
        private int releaseYear; // 개봉년도
        private int releaseMonth; // 개봉달
        private int releaseDay; // 개봉날짜
        private double gradeAvg; // 평균 평점
        private double reservationRate; // 예매율
        private long totalAudienceCnt; // 누적 관객 수
        private ScreenType screenType; // 상영 타입
    }
}
