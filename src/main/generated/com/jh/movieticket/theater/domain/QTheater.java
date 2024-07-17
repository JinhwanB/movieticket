package com.jh.movieticket.theater.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTheater is a Querydsl query type for Theater
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTheater extends EntityPathBase<Theater> {

    private static final long serialVersionUID = 1042380857L;

    public static final QTheater theater = new QTheater("theater");

    public final com.jh.movieticket.config.QBaseTimeEntity _super = new com.jh.movieticket.config.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> changeDate = _super.changeDate;

    public final DateTimePath<java.time.LocalDateTime> deleteDate = createDateTime("deleteDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> registerDate = _super.registerDate;

    public final NumberPath<Integer> seatCnt = createNumber("seatCnt", Integer.class);

    public QTheater(String variable) {
        super(Theater.class, forVariable(variable));
    }

    public QTheater(Path<? extends Theater> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTheater(PathMetadata metadata) {
        super(Theater.class, metadata);
    }

}

