package com.ll.TeamSteam.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDm is a Querydsl query type for Dm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDm extends EntityPathBase<Dm> {

    private static final long serialVersionUID = 874343837L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDm dm = new QDm("dm");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final ListPath<com.ll.TeamSteam.domain.dmMessage.entity.DmMessage, com.ll.TeamSteam.domain.dmMessage.entity.QDmMessage> dmMessages = this.<com.ll.TeamSteam.domain.dmMessage.entity.DmMessage, com.ll.TeamSteam.domain.dmMessage.entity.QDmMessage>createList("dmMessages", com.ll.TeamSteam.domain.dmMessage.entity.DmMessage.class, com.ll.TeamSteam.domain.dmMessage.entity.QDmMessage.class, PathInits.DIRECT2);

    public final com.ll.TeamSteam.domain.user.entity.QUser dmReceiver;

    public final com.ll.TeamSteam.domain.user.entity.QUser dmSender;

    public final SetPath<com.ll.TeamSteam.domain.dmUser.entity.DmUser, com.ll.TeamSteam.domain.dmUser.entity.QDmUser> dmUsers = this.<com.ll.TeamSteam.domain.dmUser.entity.DmUser, com.ll.TeamSteam.domain.dmUser.entity.QDmUser>createSet("dmUsers", com.ll.TeamSteam.domain.dmUser.entity.DmUser.class, com.ll.TeamSteam.domain.dmUser.entity.QDmUser.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public QDm(String variable) {
        this(Dm.class, forVariable(variable), INITS);
    }

    public QDm(Path<? extends Dm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDm(PathMetadata metadata, PathInits inits) {
        this(Dm.class, metadata, inits);
    }

    public QDm(Class<? extends Dm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dmReceiver = inits.isInitialized("dmReceiver") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("dmReceiver"), inits.get("dmReceiver")) : null;
        this.dmSender = inits.isInitialized("dmSender") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("dmSender"), inits.get("dmSender")) : null;
    }

}

