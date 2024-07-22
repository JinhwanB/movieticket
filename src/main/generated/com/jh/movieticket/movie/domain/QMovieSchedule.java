package com.jh.movieticket.movie.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieSchedule is a Querydsl query type for MovieSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieSchedule extends EntityPathBase<MovieSchedule> {

    private static final long serialVersionUID = -1225129072L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieSchedule movieSchedule = new QMovieSchedule("movieSchedule");

    public final com.jh.movieticket.config.QBaseTimeEntity _super = new com.jh.movieticket.config.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> changeDate = _super.changeDate;

    public final DateTimePath<java.time.LocalDateTime> deleteDate = createDateTime("deleteDate", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovie movie;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> registerDate = _super.registerDate;

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final com.jh.movieticket.theater.domain.QTheater theater;

    public QMovieSchedule(String variable) {
        this(MovieSchedule.class, forVariable(variable), INITS);
    }

    public QMovieSchedule(Path<? extends MovieSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieSchedule(PathMetadata metadata, PathInits inits) {
        this(MovieSchedule.class, metadata, inits);
    }

    public QMovieSchedule(Class<? extends MovieSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie")) : null;
        this.theater = inits.isInitialized("theater") ? new com.jh.movieticket.theater.domain.QTheater(forProperty("theater")) : null;
    }

}

