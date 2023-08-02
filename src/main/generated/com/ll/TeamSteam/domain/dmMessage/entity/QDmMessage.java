package com.ll.TeamSteam.domain.dmMessage.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDmMessage is a Querydsl query type for DmMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmMessage extends EntityPathBase<DmMessage> {

    private static final long serialVersionUID = 1747220785L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDmMessage dmMessage = new QDmMessage("dmMessage");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final com.ll.TeamSteam.domain.dm.entity.QDm dm;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final com.ll.TeamSteam.domain.dmUser.entity.QDmUser sender;

    public QDmMessage(String variable) {
        this(DmMessage.class, forVariable(variable), INITS);
    }

    public QDmMessage(Path<? extends DmMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDmMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDmMessage(PathMetadata metadata, PathInits inits) {
        this(DmMessage.class, metadata, inits);
    }

    public QDmMessage(Class<? extends DmMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dm = inits.isInitialized("dm") ? new com.ll.TeamSteam.domain.dm.entity.QDm(forProperty("dm"), inits.get("dm")) : null;
        this.sender = inits.isInitialized("sender") ? new com.ll.TeamSteam.domain.dmUser.entity.QDmUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

