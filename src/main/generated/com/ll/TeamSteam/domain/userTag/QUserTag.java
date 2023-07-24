package com.ll.TeamSteam.domain.userTag;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserTag is a Querydsl query type for UserTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserTag extends EntityPathBase<UserTag> {

    private static final long serialVersionUID = -940484702L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserTag userTag = new QUserTag("userTag");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final ListPath<com.ll.TeamSteam.domain.userTag.gameTag.GameTag, com.ll.TeamSteam.domain.userTag.gameTag.QGameTag> gameTag = this.<com.ll.TeamSteam.domain.userTag.gameTag.GameTag, com.ll.TeamSteam.domain.userTag.gameTag.QGameTag>createList("gameTag", com.ll.TeamSteam.domain.userTag.gameTag.GameTag.class, com.ll.TeamSteam.domain.userTag.gameTag.QGameTag.class, PathInits.DIRECT2);

    public final ListPath<com.ll.TeamSteam.domain.userTag.genreTag.GenreTag, com.ll.TeamSteam.domain.userTag.genreTag.QGenreTag> genreTag = this.<com.ll.TeamSteam.domain.userTag.genreTag.GenreTag, com.ll.TeamSteam.domain.userTag.genreTag.QGenreTag>createList("genreTag", com.ll.TeamSteam.domain.userTag.genreTag.GenreTag.class, com.ll.TeamSteam.domain.userTag.genreTag.QGenreTag.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QUserTag(String variable) {
        this(UserTag.class, forVariable(variable), INITS);
    }

    public QUserTag(Path<? extends UserTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserTag(PathMetadata metadata, PathInits inits) {
        this(UserTag.class, metadata, inits);
    }

    public QUserTag(Class<? extends UserTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

