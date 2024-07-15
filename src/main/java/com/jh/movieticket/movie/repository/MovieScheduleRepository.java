package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.MovieSchedule;
import com.jh.movieticket.theater.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Long> {

    boolean existsByTheater(Theater theater); // 상영관을 통해 삭제되지 않은 영화스케줄 존재 여부 확인
}
