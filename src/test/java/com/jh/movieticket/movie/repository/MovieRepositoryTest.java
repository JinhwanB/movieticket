package com.jh.movieticket.movie.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.config.JpaAuditingConfig;
import com.jh.movieticket.movie.domain.Genre;
import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.domain.MovieGenre;
import com.jh.movieticket.movie.domain.ScreenType;
import com.jh.movieticket.movie.dto.MovieSearchDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class MovieRepositoryTest {

    Pageable pageable;
    MovieSearchDto.Request searchRequest;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    GenreRepository genreRepository;

    @BeforeEach
    void before() {

        Genre genre1 = Genre.builder()
            .name("느와르")
            .build();
        Genre genre2 = Genre.builder()
            .name("공포")
            .build();
        Genre savedGenre1 = genreRepository.save(genre1);
        Genre savedGenre2 = genreRepository.save(genre2);

        Movie movie1 = Movie.builder()
            .description("description")
            .director("director")
            .gradeAvg(0)
            .posterName("poster")
            .posterUrl("url")
            .releaseDate(LocalDate.now())
            .title("title1")
            .reservationRate(0)
            .screenType(ScreenType.NOW)
            .totalAudienceCnt(0)
            .totalShowTime("100분")
            .build();
        Movie movie2 = Movie.builder()
            .description("description")
            .director("director")
            .gradeAvg(1)
            .posterName("poster")
            .posterUrl("url")
            .releaseDate(LocalDate.now())
            .title("title2")
            .reservationRate(1)
            .screenType(ScreenType.EXPECTED)
            .totalAudienceCnt(1)
            .totalShowTime("100분")
            .build();
        Movie savedMovie1 = movieRepository.save(movie1);
        Movie savedMovie2 = movieRepository.save(movie2);

        List<MovieGenre> list1 = List.of(
            MovieGenre.builder().genre(savedGenre1).movie(savedMovie1).build());
        List<MovieGenre> list2 = List.of(
            MovieGenre.builder().genre(savedGenre2).movie(savedMovie2).build());

        Movie movieWithGenre1 = savedMovie1.toBuilder()
            .movieGenreList(list1)
            .build();
        Movie movieWithGenre2 = savedMovie2.toBuilder()
            .movieGenreList(list2)
            .build();
        movieRepository.save(movieWithGenre1);
        movieRepository.save(movieWithGenre2);

        pageable = PageRequest.of(0, 10);

        searchRequest = MovieSearchDto.Request.builder()
            .orderBy("reservation")
            .title("title")
            .genre(null)
            .screenType(ScreenType.NOW)
            .build();
    }

    @Test
    @DisplayName("삭제되지 않은 영화 중 영화 이름으로 조회")
    void findByName() {

        Movie movie = movieRepository.findByTitleAndDeleteDateIsNull("title1").orElse(null);

        assertThat(movie.getTitle()).isEqualTo("title1");
    }

    @Test
    @DisplayName("삭제되지 않은 영화 중 영화 이름 중복 확인")
    void duplicatedTitle() {

        assertThat(movieRepository.existsByTitleAndDeleteDateIsNull("title1")).isEqualTo(true);
    }

    @Test
    @DisplayName("동적 검색")
    void search() {

        Page<Movie> bySearchOption = movieRepository.findBySearchOption(searchRequest, pageable);

        assertThat(bySearchOption.getTotalElements()).isEqualTo(1);
    }
}