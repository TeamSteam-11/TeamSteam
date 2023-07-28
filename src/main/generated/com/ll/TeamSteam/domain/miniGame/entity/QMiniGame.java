package com.ll.TeamSteam.domain.miniGame.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMiniGame is a Querydsl query type for MiniGame
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMiniGame extends EntityPathBase<MiniGame> {

    private static final long serialVersionUID = -1937425187L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMiniGame miniGame = new QMiniGame("miniGame");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> score = createNumber("score", Long.class);

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QMiniGame(String variable) {
        this(MiniGame.class, forVariable(variable), INITS);
    }

    public QMiniGame(Path<? extends MiniGame> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMiniGame(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMiniGame(PathMetadata metadata, PathInits inits) {
        this(MiniGame.class, metadata, inits);
    }

    public QMiniGame(Class<? extends MiniGame> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

