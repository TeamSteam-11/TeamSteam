package com.ll.TeamSteam.domain.steam.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteamGameLibrary is a Querydsl query type for SteamGameLibrary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteamGameLibrary extends EntityPathBase<SteamGameLibrary> {

    private static final long serialVersionUID = -1935559496L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteamGameLibrary steamGameLibrary = new QSteamGameLibrary("steamGameLibrary");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final NumberPath<Integer> appid = createNumber("appid", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final StringPath name = createString("name");

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QSteamGameLibrary(String variable) {
        this(SteamGameLibrary.class, forVariable(variable), INITS);
    }

    public QSteamGameLibrary(Path<? extends SteamGameLibrary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteamGameLibrary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteamGameLibrary(PathMetadata metadata, PathInits inits) {
        this(SteamGameLibrary.class, metadata, inits);
    }

    public QSteamGameLibrary(Class<? extends SteamGameLibrary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

