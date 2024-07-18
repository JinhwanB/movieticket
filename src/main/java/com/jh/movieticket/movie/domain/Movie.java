package com.jh.movieticket.movie.domain;

import com.jh.movieticket.config.BaseTimeEntity;
import com.jh.movieticket.movie.dto.MovieServiceDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String posterName; // 포스터 이미지 파일 이름

    @Column(nullable = false)
    private String posterUrl; // 포스터 이미지 링크

    @Column(nullable = false)
    private String title; // 영화 제목

    @Column(nullable = false)
    private String director; // 감독

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieActor> movieActorList; // 배우

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieGenre> movieGenreList; // 장르

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
    private LocalDateTime deleteDate; // 삭제 날짜

    /**
     * 연관관계 저장용 메소드
     *
     * @param movieActor
     */
    public void addMovieActor(MovieActor movieActor) {

        movieActorList = new ArrayList<>();
        movieActorList.add(movieActor);
    }

    /**
     * 연관관계 저장용 메소드
     *
     * @param movieGenre
     */
    public void addMovieGenre(MovieGenre movieGenre) {

        movieGenreList = new ArrayList<>();
        movieGenreList.add(movieGenre);
    }

    /**
     * Entity -> ServiceDto
     *
     * @return ServiceDto
     */
    public MovieServiceDto toServiceDto() {

        List<String> actorList =
            (movieActorList == null || movieActorList.isEmpty()) ? null : movieActorList.stream()
                .map(ma -> ma.getActor().getName())
                .toList();

        List<String> genreList =
            (movieGenreList == null || movieGenreList.isEmpty()) ? null : movieGenreList.stream()
                .map(mg -> mg.getGenre().getName())
                .toList();

        return MovieServiceDto.builder()
            .id(id)
            .posterName(posterName)
            .posterUrl(posterUrl)
            .title(title)
            .director(director)
            .actorList(actorList)
            .genreList(genreList)
            .description(description)
            .totalShowTime(totalShowTime)
            .releaseDate(releaseDate)
            .gradeAvg(gradeAvg)
            .reservationRate(reservationRate)
            .totalAudienceCnt(totalAudienceCnt)
            .screenType(screenType)
            .build();
    }
}
