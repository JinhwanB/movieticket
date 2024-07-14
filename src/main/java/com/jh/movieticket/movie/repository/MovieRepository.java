package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTitleAndDeleteDateIsNull(String title); // 삭제되지 않은 데이터 중 이름으로 조회
}
