package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.dto.MovieSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieRepositoryCustom {

    Page<Movie> findBySearchOption(MovieSearchDto.Request searchRequest, Pageable pageable);
}
