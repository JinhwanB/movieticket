package com.jh.movieticket.movie.service;

import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.movie.domain.Actor;
import com.jh.movieticket.movie.domain.Genre;
import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.domain.MovieActor;
import com.jh.movieticket.movie.domain.MovieGenre;
import com.jh.movieticket.movie.dto.MovieCreateDto;
import com.jh.movieticket.movie.dto.MovieModifyDto;
import com.jh.movieticket.movie.dto.MovieServiceDto;
import com.jh.movieticket.movie.exception.MovieErrorCode;
import com.jh.movieticket.movie.exception.MovieException;
import com.jh.movieticket.movie.repository.ActorRepository;
import com.jh.movieticket.movie.repository.GenreRepository;
import com.jh.movieticket.movie.repository.MovieRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;
    private final PosterService posterService;

    private final String IMAGE_NAME_KEY = "imageName";
    private final String IMAGE_URL_KEY = "imageUrl";

    /**
     * 영화 생성 서비스
     *
     * @param multipartFile 포스터 이미지 파일
     * @param createRequest 영화 생성 정보 dto
     * @return 생성된 영화 서비스 dto
     */
    public MovieServiceDto createMovie(MultipartFile multipartFile,
        MovieCreateDto.Request createRequest) {

        Map<String, String> uploadResult = posterService.upload(multipartFile); // 이미지 업로드

        String title = createRequest.getTitle();

        if (movieRepository.existsByTitleAndDeleteDateIsNull(title)) { // 중복된 영화 이름이 경우
            throw new MovieException(MovieErrorCode.EXIST_MOVIE_TITLE);
        }

        LocalDate releaseDate = convertToLocalDate(createRequest.getReleaseYear(),
            createRequest.getReleaseMonth(), createRequest.getReleaseDay());

        Movie movie = Movie.builder()
            .posterName(uploadResult.get(IMAGE_NAME_KEY))
            .posterUrl(uploadResult.get(IMAGE_URL_KEY))
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
     * 영화 수정 서비스
     *
     * @param multipartFile 수정할 이미지
     * @param modifyRequest 수정할 영화 정보
     * @return 수정된 영화 dto
     */
    @CacheEvict(key = "#modifyRequest.originMovieTitle", value = CacheName.MOVIE_CACHE_NAME)
    @CachePut(key = "#modifyRequest.title", value = CacheName.MOVIE_CACHE_NAME)
    public MovieServiceDto updateMovie(MultipartFile multipartFile,
        MovieModifyDto.Request modifyRequest) {

        Map<String, String> uploadResult = posterService.upload(multipartFile);

        String originMovieTitle = modifyRequest.getOriginMovieTitle();
        Movie originMovie = movieRepository.findByTitleAndDeleteDateIsNull(originMovieTitle)
            .orElseThrow(() -> new MovieException(MovieErrorCode.NOT_FOUND_MOVIE));
        if (!originMovie.getTitle().equals(modifyRequest.getTitle())
            && movieRepository.existsByTitleAndDeleteDateIsNull(
            modifyRequest.getTitle())) { // 영화 제목을 변경하고자 하며 변경할 제목이 이미 존재하는 경우
            throw new MovieException(MovieErrorCode.EXIST_MOVIE_TITLE);
        }

        posterService.deleteImage(originMovie.getPosterName()); // s3에 저장된 이미지 제거

        LocalDate releaseDate = convertToLocalDate(modifyRequest.getReleaseYear(),
            modifyRequest.getReleaseMonth(), modifyRequest.getReleaseDay());

        Movie changedMovie = originMovie.toBuilder()
            .posterName(uploadResult.get(IMAGE_NAME_KEY))
            .posterUrl(uploadResult.get(IMAGE_URL_KEY))
            .title(modifyRequest.getTitle())
            .director(modifyRequest.getDirector())
            .description(modifyRequest.getDescription())
            .totalShowTime(modifyRequest.getTotalShowTime())
            .releaseDate(releaseDate)
            .screenType(modifyRequest.getScreenType())
            .build();

        List<MovieActor> movieActorList = getMovieActorList(changedMovie,
            modifyRequest.getActorList());
        List<MovieGenre> movieGenreList = getMovieGenreList(changedMovie,
            modifyRequest.getGenreList());

        Movie changedMovieWithActorAndGenre = changedMovie.toBuilder()
            .movieActorList(movieActorList)
            .movieGenreList(movieGenreList)
            .build();
        Movie modifiedMovie = movieRepository.save(changedMovieWithActorAndGenre);

        return modifiedMovie.toServiceDto();
    }

    /**
     * 영화 삭제 서비스
     *
     * @param movieTitle 삭제할 영화 제목
     */
    @CacheEvict(key = "#movieTitle", value = CacheName.MOVIE_CACHE_NAME)
    public void deleteMovie(String movieTitle) {

        Movie movie = movieRepository.findByTitleAndDeleteDateIsNull(movieTitle)
            .orElseThrow(() -> new MovieException(MovieErrorCode.NOT_FOUND_MOVIE));

        Movie deletedMovie = movie.toBuilder()
            .deleteDate(LocalDateTime.now())
            .movieActorList(null)
            .movieGenreList(null)
            .build();
        movieRepository.save(deletedMovie);
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
