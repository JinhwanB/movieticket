package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {

    Optional<Movie> findByIdAndDeleteDateIsNull(Long id); // 삭제되지 않은 데이터 중 pk로 조회

    Optional<Movie> findByTitleAndDeleteDateIsNull(String title); // 삭제되지 않은 데이터 중 이름으로 조회

    boolean existsByTitleAndDeleteDateIsNull(String title); // 중복 이름인지 확인
}
