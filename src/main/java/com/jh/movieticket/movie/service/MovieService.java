package com.jh.movieticket.movie.service;

import com.jh.movieticket.movie.domain.Actor;
import com.jh.movieticket.movie.domain.Genre;
import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.domain.MovieActor;
import com.jh.movieticket.movie.domain.MovieGenre;
import com.jh.movieticket.movie.dto.MovieCreateDto;
import com.jh.movieticket.movie.dto.MovieServiceDto;
import com.jh.movieticket.movie.exception.MovieErrorCode;
import com.jh.movieticket.movie.exception.MovieException;
import com.jh.movieticket.movie.repository.ActorRepository;
import com.jh.movieticket.movie.repository.GenreRepository;
import com.jh.movieticket.movie.repository.MovieRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;
    private final PosterService posterService;

    /**
     * 영화 생성 서비스
     *
     * @param multipartFile 포스터 이미지 파일
     * @param createRequest 영화 생성 정보 dto
     * @return 생성된 영화 서비스 dto
     */
    public MovieServiceDto createMovie(MultipartFile multipartFile, MovieCreateDto.Request createRequest) {

        Map<String, String> uploadResult = posterService.upload(multipartFile); // 이미지 업로드

        String title = createRequest.getTitle();

        if (movieRepository.existsByTitleAndDeleteDateIsNull(title)) { // 중복된 영화 이름이 경우
            throw new MovieException(MovieErrorCode.EXIST_MOVIE_TITLE);
        }

        LocalDate releaseDate = convertToLocalDate(createRequest.getReleaseYear(),
            createRequest.getReleaseMonth(), createRequest.getReleaseDay());

        Movie movie = Movie.builder()
            .posterName(uploadResult.get("imageName"))
            .posterUrl(uploadResult.get("imageUrl"))
            .title(title)
            .director(createRequest.getDirector())
            .description(createRequest.getDescription())
            .totalShowTime(createRequest.getTotalShowTime())
            .releaseDate(releaseDate)
            .gradeAvg(0)
            .reservationRate(0)
            .totalAudienceCnt(0)
            .screenType(createRequest.getScreenType())
            .build();
        Movie savedMovie = movieRepository.save(movie);

        List<MovieActor> movieActorList = getMovieActorList(savedMovie,
            createRequest.getActorList());
        List<MovieGenre> movieGenreList = getMovieGenreList(savedMovie,
            createRequest.getGenreList());

        Movie movieWithActorAndGenre = savedMovie.toBuilder()
            .movieActorList(movieActorList)
            .movieGenreList(movieGenreList)
            .build();
        Movie finSave = movieRepository.save(movieWithActorAndGenre);

        return finSave.toServiceDto();
    }

    /**
     * 장르 엔티티 저장하고 영화-장르 중간 엔티티 반환
     *
     * @param movie         영화
     * @param genreNameList 장르 이름 리스트
     * @return 영화-장르 중간 엔티티 리스트
     */
    private List<MovieGenre> getMovieGenreList(Movie movie, List<String> genreNameList) {

        List<Genre> genreList = genreNameList.stream()
            .map(g -> Genre.builder()
                .name(g)
                .build())
            .toList();

        List<Genre> genres = genreRepository.saveAll(genreList);

        return genres.stream()
            .map(g -> MovieGenre.builder()
                .genre(g)
                .movie(movie)
                .build())
            .toList();
    }

    /**
     * 배우 엔티티 저장하고 영화-배우 중간 엔티티 반환
     *
     * @param movie         영화
     * @param actorNameList 배우 이름 리스트
     * @return 영화-배우 중간 엔티티 리스트
     */
    private List<MovieActor> getMovieActorList(Movie movie, List<String> actorNameList) {

        List<Actor> actorList = actorNameList.stream()
            .map(a -> Actor.builder()
                .name(a)
                .build())
            .toList();

        List<Actor> actors = actorRepository.saveAll(actorList);

        return actors.stream()
            .map(a -> MovieActor.builder()
                .actor(a)
                .movie(movie)
                .build())
            .toList();
    }

    /**
     * LocalDate 반환 메소드
     *
     * @param year  년도
     * @param month 달
     * @param day   날짜
     * @return LocalDate
     */
    private LocalDate convertToLocalDate(int year, int month, int day) {

        return LocalDate.of(year, month, day);
    }
}
