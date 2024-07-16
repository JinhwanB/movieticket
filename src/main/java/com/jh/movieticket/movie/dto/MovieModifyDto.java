package com.jh.movieticket.movie.dto;

import com.jh.movieticket.movie.domain.ScreenType;
import com.jh.movieticket.validation.IsEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 영화 수정 시 dto
public class MovieModifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request{

        @NotBlank(message = "변경을 원하는 영화의 제목을 입력하세요.")
        private String originMovieTitle; // 변경할 영화 제목

        @NotBlank(message = "영화 제목을 입력해주세요.")
        private String title; // 영화 제목

        @NotBlank(message = "영화 감독을 입력해주세요.")
        private String director; // 감독

        @NotEmpty(message = "배우를 입력해주세요.")
        private List<@NotBlank(message = "배우를 입력해주세요.") String> actorList; // 배우

        @NotEmpty(message = "장르를 입력해주세요.")
        private List<@NotBlank(message = "장르를 입력해주세요.") String> genreList; // 장르

        @NotBlank(message = "영화에 대한 설명을 입력해주세요.")
        private String description; // 영화에 대한 설명

        @NotBlank(message = "총 상영 시간을 입력해주세요.")
        @Pattern(regexp = "^[1-9][0-9]*분$", message = "올바른 총 상영 시간을 입력해주세요. (ex. 59분, 190분)")
        private String totalShowTime; // 총 상영 시간

        @Min(value = 1000, message = "개봉년도는 1000보다 크거나 같은 숫자여야 합니다.")
        @Max(value = 9999, message = "개봉년도는 9999보다 작거나 같은 숫자여야 합니다.")
        private int releaseYear; // 개봉년도

        @Min(value = 1, message = "개봉달은 1보다 크거나 같은 숫자여야 합니다.")
        @Max(value = 12, message = "개봉달은 12보다 작거나 같은 숫자여야 합니다.")
        private int releaseMonth; // 개봉달

        @Min(value = 1, message = "개봉날짜는 1보다 크거나 같은 숫자여야 합니다.")
        @Max(value = 31, message = "개봉날짜는 31보다 작거나 같은 숫자여야 합니다.")
        private int releaseDay; // 개봉날짜

        @IsEnum(message = "올바른 상영 타입을 입력하세요. (ex. now, previous, expected)")
        private ScreenType screenType; // 상영 타입
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

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
