package com.ll.TeamSteam.domain.matchingPartner.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchingPartner is a Querydsl query type for MatchingPartner
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchingPartner extends EntityPathBase<MatchingPartner> {

    private static final long serialVersionUID = 216958417L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatchingPartner matchingPartner = new QMatchingPartner("matchingPartner");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath inChatRoomTrueFalse = createBoolean("inChatRoomTrueFalse");

    public final com.ll.TeamSteam.domain.matching.entity.QMatching matching;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final ListPath<com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser, com.ll.TeamSteam.domain.recentlyUser.entity.QRecentlyUser> recentlyUserList = this.<com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser, com.ll.TeamSteam.domain.recentlyUser.entity.QRecentlyUser>createList("recentlyUserList", com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser.class, com.ll.TeamSteam.domain.recentlyUser.entity.QRecentlyUser.class, PathInits.DIRECT2);

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QMatchingPartner(String variable) {
        this(MatchingPartner.class, forVariable(variable), INITS);
    }

    public QMatchingPartner(Path<? extends MatchingPartner> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatchingPartner(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatchingPartner(PathMetadata metadata, PathInits inits) {
        this(MatchingPartner.class, metadata, inits);
    }

    public QMatchingPartner(Class<? extends MatchingPartner> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.matching = inits.isInitialized("matching") ? new com.ll.TeamSteam.domain.matching.entity.QMatching(forProperty("matching"), inits.get("matching")) : null;
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

