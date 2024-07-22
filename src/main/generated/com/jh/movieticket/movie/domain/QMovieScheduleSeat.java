package com.jh.movieticket.movie.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieScheduleSeat is a Querydsl query type for MovieScheduleSeat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieScheduleSeat extends EntityPathBase<MovieScheduleSeat> {

    private static final long serialVersionUID = -598409803L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieScheduleSeat movieScheduleSeat = new QMovieScheduleSeat("movieScheduleSeat");

    public final com.jh.movieticket.config.QBaseTimeEntity _super = new com.jh.movieticket.config.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> changeDate = _super.changeDate;

    public final DateTimePath<java.time.LocalDateTime> deleteDate = createDateTime("deleteDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovieSchedule movieSchedule;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> registerDate = _super.registerDate;

    public final com.jh.movieticket.theater.domain.QSeat seat;

    public final EnumPath<SeatType> status = createEnum("status", SeatType.class);

    public QMovieScheduleSeat(String variable) {
        this(MovieScheduleSeat.class, forVariable(variable), INITS);
    }

    public QMovieScheduleSeat(Path<? extends MovieScheduleSeat> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieScheduleSeat(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieScheduleSeat(PathMetadata metadata, PathInits inits) {
        this(MovieScheduleSeat.class, metadata, inits);
    }

    public QMovieScheduleSeat(Class<? extends MovieScheduleSeat> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.movieSchedule = inits.isInitialized("movieSchedule") ? new QMovieSchedule(forProperty("movieSchedule"), inits.get("movieSchedule")) : null;
        this.seat = inits.isInitialized("seat") ? new com.jh.movieticket.theater.domain.QSeat(forProperty("seat"), inits.get("seat")) : null;
    }

}

