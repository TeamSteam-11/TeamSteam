package com.ll.TeamSteam.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = -155412767L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> dmId = createNumber("dmId", Long.class);

    public final BooleanPath enterAlarm = createBoolean("enterAlarm");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.ll.TeamSteam.domain.user.entity.QUser invitedUser;

    public final com.ll.TeamSteam.domain.user.entity.QUser invitingUser;

    public final StringPath matchingName = createString("matchingName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final DateTimePath<java.time.LocalDateTime> readDate = createDateTime("readDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> roomId = createNumber("roomId", Long.class);

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.invitedUser = inits.isInitialized("invitedUser") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("invitedUser"), inits.get("invitedUser")) : null;
        this.invitingUser = inits.isInitialized("invitingUser") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("invitingUser"), inits.get("invitingUser")) : null;
    }

}

