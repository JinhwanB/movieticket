package com.jh.movieticket.movie.repository;

import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.domain.ScreenType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieRepositoryCustom {

    Page<Movie> findBySearchOption(String title, ScreenType screenType, String genre, String orderBy, Pageable pageable);
}
