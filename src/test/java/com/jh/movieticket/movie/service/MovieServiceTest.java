package com.jh.movieticket.movie.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.domain.ScreenType;
import com.jh.movieticket.movie.dto.MovieCreateDto;
import com.jh.movieticket.movie.dto.MovieModifyDto;
import com.jh.movieticket.movie.dto.MovieSearchDto;
import com.jh.movieticket.movie.dto.MovieServiceDto;
import com.jh.movieticket.movie.exception.MovieException;
import com.jh.movieticket.movie.exception.PosterException;
import com.jh.movieticket.movie.repository.MovieRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MovieServiceTest {

    MovieService movieService;
    MovieServiceDto movieServiceDto;
    Map<String, String> uploadResult;
    Movie movie;
    MovieCreateDto.Request createRequest;
    MovieModifyDto.Request modifyRequest;
    MovieSearchDto.Request searchRequest;
    Pageable pageable;
    Page<Movie> searchResult;

    @MockBean
    MovieRepository movieRepository;

    @MockBean
    PosterService posterService;

    @MockBean
    CacheManager redisCacheManager;

    @BeforeEach
    void before() {

        movieService = new MovieService(movieRepository, posterService, redisCacheManager);

        List<String> genreList = List.of("genre1", "genre2");
        List<String> actorList = List.of("actor1", "actor2");

        movieServiceDto = MovieServiceDto.builder()
            .title("title")
            .description("description")
            .director("director")
            .screenType(ScreenType.NOW)
            .totalShowTime("100분")
            .releaseDate(LocalDate.now())
            .genreList(genreList)
            .actorList(actorList)
            .posterName("posterName")
            .totalAudienceCnt(0)
            .reservationRate(0)
            .gradeAvg(0)
            .posterUrl("posterUrl")
            .build();

        uploadResult = new HashMap<>();
        uploadResult.put("imageUrl", "imageUrl");
        uploadResult.put("imageName", "imageName");

        movie = Movie.builder()
            .title("title")
            .description("description")
            .director("director")
            .screenType(ScreenType.NOW)
            .totalShowTime("100분")
            .releaseDate(LocalDate.now())
            .posterName("posterName")
            .totalAudienceCnt(0)
            .reservationRate(0)
            .gradeAvg(0)
            .posterUrl("posterUrl")
            .build();

        createRequest = MovieCreateDto.Request.builder()
            .title("title")
            .description("description")
            .director("director")
            .screenType(ScreenType.NOW)
            .totalShowTime("100분")
            .releaseDay(15)
            .releaseMonth(1)
            .releaseYear(2023)
            .genreList(genreList)
            .actorList(actorList)
            .build();

        modifyRequest = MovieModifyDto.Request.builder()
            .title("title2")
            .description("description")
            .director("director")
            .screenType(ScreenType.NOW)
            .totalShowTime("100분")
            .releaseDay(15)
            .releaseMonth(1)
            .releaseYear(2023)
            .genreList(genreList)
            .actorList(actorList)
            .originMovieTitle("title")
            .build();

        searchRequest = MovieSearchDto.Request.builder()
            .title("title")
            .orderBy("reservation")
            .genre("genre")
            .screenType(ScreenType.NOW)
            .build();

        pageable = PageRequest.of(0, 10);

        List<Movie> movieList = List.of(movie);
        searchResult = new PageImpl<>(movieList, pageable, movieList.size());
    }

    @Test
    @DisplayName("영화 생성 서비스")
    void movieCreateService() {

        when(posterService.upload(any())).thenReturn(uploadResult);
        when(movieRepository.existsByTitle(any())).thenReturn(false);
        when(movieRepository.save(any())).thenReturn(movie);

        MovieServiceDto serviceDto = movieService.createMovie(any(), createRequest);

        assertThat(serviceDto.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("영화 생성 서비스 실패 - 포스터 업로드 실패")
    void movieCreateServiceFail1() {

        when(posterService.upload(any())).thenThrow(PosterException.class);

        assertThatThrownBy(() -> movieService.createMovie(any(), createRequest)).isInstanceOf(
            PosterException.class);
    }

    @Test
    @DisplayName("영화 생성 서비스 실패 - 영화 이름 중복")
    void movieCreateServiceFail2() {

        when(posterService.upload(any())).thenReturn(uploadResult);
        when(movieRepository.existsByTitle(any())).thenReturn(true);

        assertThatThrownBy(() -> movieService.createMovie(any(), createRequest)).isInstanceOf(
            MovieException.class);
    }

    @Test
    @DisplayName("영화 수정 서비스")
    void movieModifyService() {

        when(posterService.upload(any())).thenReturn(uploadResult);
        when(movieRepository.findByTitle(any())).thenReturn(Optional.of(movie));
        when(movieRepository.existsByTitle(any())).thenReturn(false);
        when(movieRepository.save(any())).thenReturn(movie.toBuilder().title("title2").build());

        MovieServiceDto serviceDto = movieService.updateMovie(any(), modifyRequest);

        assertThat(serviceDto.getTitle()).isEqualTo("title2");
    }

    @Test
    @DisplayName("영화 수정 서비스 실패 - 포스터 업로드 실패")
    void movieModifyServiceFail1() {

        when(posterService.upload(any())).thenThrow(PosterException.class);

        assertThatThrownBy(() -> movieService.updateMovie(any(), modifyRequest)).isInstanceOf(
            PosterException.class);
    }

    @Test
    @DisplayName("영화 수정 서비스 실패 - 없는 영화")
    void movieModifyServiceFail2() {

        when(posterService.upload(any())).thenReturn(uploadResult);
        when(movieRepository.findByTitle(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.updateMovie(any(), modifyRequest)).isInstanceOf(
            MovieException.class);
    }

    @Test
    @DisplayName("영화 수정 서비스 실패 - 중복된 영화 이름")
    void movieModifyServiceFail3() {

        when(posterService.upload(any())).thenReturn(uploadResult);
        when(movieRepository.findByTitle(any())).thenReturn(Optional.of(movie));
        when(movieRepository.existsByTitle(any())).thenReturn(true);

        assertThatThrownBy(() -> movieService.updateMovie(any(), modifyRequest)).isInstanceOf(
            MovieException.class);
    }

    @Test
    @DisplayName("영화 삭제 서비스")
    void movieDeleteService() {

        when(movieRepository.findById(any())).thenReturn(Optional.of(movie));

        movieService.deleteMovie(any());

        verify(movieRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("영화 삭제 서비스 실패 - 없는 영화")
    void movieDeleteServiceFail1() {

        when(movieRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.deleteMovie(any())).isInstanceOf(
            MovieException.class);
    }

    @Test
    @DisplayName("영화 조회 서비스")
    void movieVerifyService() {

        when(movieRepository.findByTitle(any())).thenReturn(Optional.of(movie));

        MovieServiceDto serviceDto = movieService.verifyMovie("title");

        assertThat(serviceDto.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("영화 조회 서비스 실패 - 없는 영화")
    void movieVerifyServiceFail() {

        when(movieRepository.findByTitle(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.verifyMovie(any())).isInstanceOf(
            MovieException.class);
    }

    @Test
    @DisplayName("영화 검색 서비스")
    void movieSearchService() {

        when(movieRepository.findBySearchOption(any(), any())).thenReturn(searchResult);

        Page<MovieServiceDto> movieServiceDtos = movieService.searchMovie(searchRequest, pageable);

        assertThat(movieServiceDtos.getTotalElements()).isEqualTo(1);
    }
}