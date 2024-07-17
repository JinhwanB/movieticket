package com.jh.movieticket.movie.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovie is a Querydsl query type for Movie
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovie extends EntityPathBase<Movie> {

    private static final long serialVersionUID = 1534850521L;

    public static final QMovie movie = new QMovie("movie");

    public final com.jh.movieticket.config.QBaseTimeEntity _super = new com.jh.movieticket.config.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> changeDate = _super.changeDate;

    public final DateTimePath<java.time.LocalDateTime> deleteDate = createDateTime("deleteDate", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final StringPath director = createString("director");

    public final NumberPath<Double> gradeAvg = createNumber("gradeAvg", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MovieActor, QMovieActor> movieActorList = this.<MovieActor, QMovieActor>createList("movieActorList", MovieActor.class, QMovieActor.class, PathInits.DIRECT2);

    public final ListPath<MovieGenre, QMovieGenre> movieGenreList = this.<MovieGenre, QMovieGenre>createList("movieGenreList", MovieGenre.class, QMovieGenre.class, PathInits.DIRECT2);

    public final StringPath posterName = createString("posterName");

    public final StringPath posterUrl = createString("posterUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> registerDate = _super.registerDate;

    public final DatePath<java.time.LocalDate> releaseDate = createDate("releaseDate", java.time.LocalDate.class);

    public final NumberPath<Double> reservationRate = createNumber("reservationRate", Double.class);

    public final EnumPath<ScreenType> screenType = createEnum("screenType", ScreenType.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> totalAudienceCnt = createNumber("totalAudienceCnt", Long.class);

    public final StringPath totalShowTime = createString("totalShowTime");

    public QMovie(String variable) {
        super(Movie.class, forVariable(variable));
    }

    public QMovie(Path<? extends Movie> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMovie(PathMetadata metadata) {
        super(Movie.class, metadata);
    }

}

