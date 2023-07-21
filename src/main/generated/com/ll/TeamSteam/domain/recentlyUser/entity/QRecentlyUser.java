package com.ll.TeamSteam.domain.recentlyUser.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecentlyUser is a Querydsl query type for RecentlyUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecentlyUser extends EntityPathBase<RecentlyUser> {

    private static final long serialVersionUID = -1271182543L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecentlyUser recentlyUser = new QRecentlyUser("recentlyUser");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner matchingPartner;

    public final StringPath matchingPartnerName = createString("matchingPartnerName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QRecentlyUser(String variable) {
        this(RecentlyUser.class, forVariable(variable), INITS);
    }

    public QRecentlyUser(Path<? extends RecentlyUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecentlyUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecentlyUser(PathMetadata metadata, PathInits inits) {
        this(RecentlyUser.class, metadata, inits);
    }

    public QRecentlyUser(Class<? extends RecentlyUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.matchingPartner = inits.isInitialized("matchingPartner") ? new com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner(forProperty("matchingPartner"), inits.get("matchingPartner")) : null;
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

