package com.ll.TeamSteam.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 252145825L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final StringPath avatar = createString("avatar");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final ListPath<com.ll.TeamSteam.domain.friend.entity.Friend, com.ll.TeamSteam.domain.friend.entity.QFriend> friendList = this.<com.ll.TeamSteam.domain.friend.entity.Friend, com.ll.TeamSteam.domain.friend.entity.QFriend>createList("friendList", com.ll.TeamSteam.domain.friend.entity.Friend.class, com.ll.TeamSteam.domain.friend.entity.QFriend.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final ListPath<com.ll.TeamSteam.domain.matching.entity.Matching, com.ll.TeamSteam.domain.matching.entity.QMatching> matchingList = this.<com.ll.TeamSteam.domain.matching.entity.Matching, com.ll.TeamSteam.domain.matching.entity.QMatching>createList("matchingList", com.ll.TeamSteam.domain.matching.entity.Matching.class, com.ll.TeamSteam.domain.matching.entity.QMatching.class, PathInits.DIRECT2);

    public final ListPath<com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner, com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner> matchingPartners = this.<com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner, com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner>createList("matchingPartners", com.ll.TeamSteam.domain.matchingPartner.entity.MatchingPartner.class, com.ll.TeamSteam.domain.matchingPartner.entity.QMatchingPartner.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final ListPath<com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser, com.ll.TeamSteam.domain.recentlyUser.entity.QRecentlyUser> recentlyUsers = this.<com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser, com.ll.TeamSteam.domain.recentlyUser.entity.QRecentlyUser>createList("recentlyUsers", com.ll.TeamSteam.domain.recentlyUser.entity.RecentlyUser.class, com.ll.TeamSteam.domain.recentlyUser.entity.QRecentlyUser.class, PathInits.DIRECT2);

    public final ListPath<com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary, com.ll.TeamSteam.domain.steam.entity.QSteamGameLibrary> steamGameLibrary = this.<com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary, com.ll.TeamSteam.domain.steam.entity.QSteamGameLibrary>createList("steamGameLibrary", com.ll.TeamSteam.domain.steam.entity.SteamGameLibrary.class, com.ll.TeamSteam.domain.steam.entity.QSteamGameLibrary.class, PathInits.DIRECT2);

    public final StringPath steamId = createString("steamId");

    public final NumberPath<Integer> temperature = createNumber("temperature", Integer.class);

    public final EnumPath<Gender> type = createEnum("type", Gender.class);

    public final StringPath username = createString("username");

    public final com.ll.TeamSteam.domain.userTag.entity.QUserTag userTag;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userTag = inits.isInitialized("userTag") ? new com.ll.TeamSteam.domain.userTag.entity.QUserTag(forProperty("userTag"), inits.get("userTag")) : null;
    }

}

