package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.MovieSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Long> {

    @Query("select ms from MovieSchedule ms where ms.theater.id = :theaterId and ms.deleteDate is null")
    List<MovieSchedule> findByTheaterId(Long theaterId); // 상영관 id를 통해 삭제되지 않은 영화스케줄 조회
}
