package com.jh.movieticket.movie.repository;

import static com.jh.movieticket.movie.domain.QMovie.movie;
import static com.jh.movieticket.movie.domain.QMovieActor.movieActor;
import static com.jh.movieticket.movie.domain.QMovieGenre.movieGenre;

import com.jh.movieticket.movie.domain.Movie;
import com.jh.movieticket.movie.domain.ScreenType;
import com.jh.movieticket.movie.dto.MovieSearchDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class MovieRepositoryCustomImpl implements MovieRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public MovieRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Movie> findBySearchOption(MovieSearchDto.Request searchRequest, Pageable pageable) {

        List<Movie> movies = jpaQueryFactory.selectFrom(movie)
            .leftJoin(movie.movieGenreList, movieGenre)
            .leftJoin(movie.movieActorList, movieActor)
            .where(containsTitle(searchRequest.getTitle()),
                eqScreenType(searchRequest.getScreenType()), eqGenre(searchRequest.getGenre()),
                movie.deleteDate.isNull())
            .orderBy(getOrderSpecifier(searchRequest.getOrderBy()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(movies, pageable, movies.size());
    }

    private BooleanExpression containsTitle(String title) {

        if (!StringUtils.hasText(title)) {
            return null;
        }

        return movie.title.containsIgnoreCase(title);
    }

    private BooleanExpression eqScreenType(ScreenType screenType) {

        if (screenType == null) {
            return null;
        }

        return movie.screenType.eq(screenType);
    }

    private BooleanExpression eqGenre(String genre) {

        if (!StringUtils.hasText(genre)) {
            return null;
        }

        return movieGenre.genre.name.eq(genre);
    }

    private OrderSpecifier<?> getOrderSpecifier(String orderBy) {

        final String GRADE = "grade";
        final String RESERVATION = "reservation";
        final String AUDIENCE = "audience";

        return switch (orderBy) {
            case GRADE -> movie.gradeAvg.desc();
            case RESERVATION -> movie.reservationRate.desc();
            case AUDIENCE -> movie.totalAudienceCnt.desc();
            default -> movie.title.asc();
        };

    }
}
