package com.ll.TeamSteam.domain.userTag.gameTag;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGameTag is a Querydsl query type for GameTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameTag extends EntityPathBase<GameTag> {

    private static final long serialVersionUID = -572099211L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGameTag gameTag = new QGameTag("gameTag");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final NumberPath<Integer> appid = createNumber("appid", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final StringPath name = createString("name");

    public final com.ll.TeamSteam.domain.userTag.QUserTag userTag;

    public QGameTag(String variable) {
        this(GameTag.class, forVariable(variable), INITS);
    }

    public QGameTag(Path<? extends GameTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGameTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGameTag(PathMetadata metadata, PathInits inits) {
        this(GameTag.class, metadata, inits);
    }

    public QGameTag(Class<? extends GameTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userTag = inits.isInitialized("userTag") ? new com.ll.TeamSteam.domain.userTag.QUserTag(forProperty("userTag"), inits.get("userTag")) : null;
    }

}

