package com.ll.TeamSteam.domain.matching.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatching is a Querydsl query type for Matching
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatching extends EntityPathBase<Matching> {

    private static final long serialVersionUID = 677391109L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatching matching = new QMatching("matching");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final NumberPath<Long> capacity = createNumber("capacity", Long.class);

    public final com.ll.TeamSteam.domain.chatRoom.entity.QChatRoom chatRoom;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final DateTimePath<java.time.LocalDateTime> deadlineDate = createDateTime("deadlineDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> endTime = createNumber("endTime", Integer.class);

    public final NumberPath<Integer> gameTagId = createNumber("gameTagId", Integer.class);

    public final StringPath gameTagName = createString("gameTagName");

    public final StringPath gender = createString("gender");

    public final EnumPath<com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType> genre = createEnum("genre", com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isMic = createBoolean("isMic");

    public final ListPath<com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner, com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner> matchingPartners = this.<com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner, com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner>createList("matchingPartners", com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner.class, com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> participant = createNumber("participant", Long.class);

    public final NumberPath<Integer> startTime = createNumber("startTime", Integer.class);

    public final StringPath title = createString("title");

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QMatching(String variable) {
        this(Matching.class, forVariable(variable), INITS);
    }

    public QMatching(Path<? extends Matching> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatching(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatching(PathMetadata metadata, PathInits inits) {
        this(Matching.class, metadata, inits);
    }

    public QMatching(Class<? extends Matching> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new com.ll.TeamSteam.domain.chatRoom.entity.QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

