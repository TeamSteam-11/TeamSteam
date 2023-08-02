package com.ll.TeamSteam.domain.dmUser.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDmUser is a Querydsl query type for DmUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmUser extends EntityPathBase<DmUser> {

    private static final long serialVersionUID = 1570475059L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDmUser dmUser = new QDmUser("dmUser");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final com.ll.TeamSteam.domain.dm.entity.QDm dm;

    public final ListPath<com.ll.TeamSteam.domain.dmMessage.entity.DmMessage, com.ll.TeamSteam.domain.dmMessage.entity.QDmMessage> dmMessages = this.<com.ll.TeamSteam.domain.dmMessage.entity.DmMessage, com.ll.TeamSteam.domain.dmMessage.entity.QDmMessage>createList("dmMessages", com.ll.TeamSteam.domain.dmMessage.entity.DmMessage.class, com.ll.TeamSteam.domain.dmMessage.entity.QDmMessage.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QDmUser(String variable) {
        this(DmUser.class, forVariable(variable), INITS);
    }

    public QDmUser(Path<? extends DmUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDmUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDmUser(PathMetadata metadata, PathInits inits) {
        this(DmUser.class, metadata, inits);
    }

    public QDmUser(Class<? extends DmUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dm = inits.isInitialized("dm") ? new com.ll.TeamSteam.domain.dm.entity.QDm(forProperty("dm"), inits.get("dm")) : null;
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

