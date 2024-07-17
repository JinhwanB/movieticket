package com.jh.movieticket.theater.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSeat is a Querydsl query type for Seat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeat extends EntityPathBase<Seat> {

    private static final long serialVersionUID = 1572896477L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSeat seat = new QSeat("seat");

    public final com.jh.movieticket.config.QBaseTimeEntity _super = new com.jh.movieticket.config.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> changeDate = _super.changeDate;

    public final DateTimePath<java.time.LocalDateTime> deleteDate = createDateTime("deleteDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> registerDate = _super.registerDate;

    public final NumberPath<Integer> seatNo = createNumber("seatNo", Integer.class);

    public final QTheater theater;

    public QSeat(String variable) {
        this(Seat.class, forVariable(variable), INITS);
    }

    public QSeat(Path<? extends Seat> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSeat(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSeat(PathMetadata metadata, PathInits inits) {
        this(Seat.class, metadata, inits);
    }

    public QSeat(Class<? extends Seat> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.theater = inits.isInitialized("theater") ? new QTheater(forProperty("theater")) : null;
    }

}

