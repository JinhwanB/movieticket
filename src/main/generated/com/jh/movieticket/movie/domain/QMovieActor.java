package com.jh.movieticket.movie.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieActor is a Querydsl query type for MovieActor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieActor extends EntityPathBase<MovieActor> {

    private static final long serialVersionUID = 583238556L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieActor movieActor = new QMovieActor("movieActor");

    public final com.jh.movieticket.config.QBaseTimeEntity _super = new com.jh.movieticket.config.QBaseTimeEntity(this);

    public final QActor actor;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> changeDate = _super.changeDate;

    public final DateTimePath<java.time.LocalDateTime> deleteDate = createDateTime("deleteDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovie movie;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> registerDate = _super.registerDate;

    public QMovieActor(String variable) {
        this(MovieActor.class, forVariable(variable), INITS);
    }

    public QMovieActor(Path<? extends MovieActor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieActor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieActor(PathMetadata metadata, PathInits inits) {
        this(MovieActor.class, metadata, inits);
    }

    public QMovieActor(Class<? extends MovieActor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.actor = inits.isInitialized("actor") ? new QActor(forProperty("actor")) : null;
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie")) : null;
    }

}

