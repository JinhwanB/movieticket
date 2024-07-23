package com.jh.movieticket.movie.domain;

import com.jh.movieticket.config.BaseTimeEntity;
import com.jh.movieticket.movie.dto.MovieScheduleServiceDto;
import com.jh.movieticket.theater.domain.Theater;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 영화별 상영 스케줄 테이블
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class MovieSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie; // 영화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater; // 상영관

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movieSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieScheduleSeat> movieScheduleSeatList;

    @Column(nullable = false)
    private LocalDate endDate; // 종영 날짜

    @Column(nullable = false)
    private LocalDateTime startTime; // 시작시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 끝나는 시간

    @Column
    private LocalDateTime deleteDate; // 삭제날짜

    /**
     * Entity -> ServiceDto
     *
     * @return ServiceDto
     */
    public MovieScheduleServiceDto toServiceDto() {

        return MovieScheduleServiceDto.builder()
            .id(id)
            .movie(movie.toServiceDto())
            .endDate(endDate)
            .endTime(endTime)
            .startTime(startTime)
            .theater(theater.toServiceDto())
            .build();
    }
}
