package com.ll.TeamSteam.domain.userTag.genreTag;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGenreTag is a Querydsl query type for GenreTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGenreTag extends EntityPathBase<GenreTag> {

    private static final long serialVersionUID = 309708897L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGenreTag genreTag = new QGenreTag("genreTag");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final EnumPath<com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType> genre = createEnum("genre", com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final com.ll.TeamSteam.domain.userTag.QUserTag userTag;

    public QGenreTag(String variable) {
        this(GenreTag.class, forVariable(variable), INITS);
    }

    public QGenreTag(Path<? extends GenreTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGenreTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGenreTag(PathMetadata metadata, PathInits inits) {
        this(GenreTag.class, metadata, inits);
    }

    public QGenreTag(Class<? extends GenreTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userTag = inits.isInitialized("userTag") ? new com.ll.TeamSteam.domain.userTag.QUserTag(forProperty("userTag"), inits.get("userTag")) : null;
    }

}

