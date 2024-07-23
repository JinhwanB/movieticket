package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.MovieScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScheduleSeatRepository extends JpaRepository<MovieScheduleSeat, Long> {

}
