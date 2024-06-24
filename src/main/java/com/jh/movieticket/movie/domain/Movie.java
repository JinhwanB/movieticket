package com.jh.movieticket.movie.domain;

import com.jh.movieticket.config.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Table(
    name = "movie",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "movieUnique",
            columnNames = {"title", "del_date"}
        )
    }
)
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String posterUrl; // 포스터 이미지 링크

    @Column(nullable = false)
    private String title; // 영화 제목

    @Column(nullable = false)
    private String director; // 감독

    @Column(nullable = false, length = 5000)
    private String description; // 영화에 대한 설명

    @Column(nullable = false)
    private String totalShowTime; // 총 상영 시간

    @Column(nullable = false)
    private LocalDate releaseDate; // 개봉 날짜

    @Column(nullable = false)
    private double gradeAvg; // 평균 평점

    @Column(nullable = false)
    private double reservationRate; // 예매율

    @Column(nullable = false)
    private long totalAudienceCnt; // 누적 관객 수

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScreenType screenType; // 상영 타입

    @Column
    private LocalDateTime delDate; // 삭제 날짜
}
