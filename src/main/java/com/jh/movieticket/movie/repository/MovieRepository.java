package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {


    Optional<Movie> findByTitle(String title); // 영화 제목으로 조회

    boolean existsByTitle(String title); // 중복 이름인지 확인
}
