package com.ll.TeamSteam.domain.matchingTag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMatchingTag is a Querydsl query type for MatchingTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchingTag extends EntityPathBase<MatchingTag> {

    private static final long serialVersionUID = -1490711919L;

    public static final QMatchingTag matchingTag = new QMatchingTag("matchingTag");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath gameTag = createString("gameTag");

    public final EnumPath<GenreTagType> genre = createEnum("genre", GenreTagType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public QMatchingTag(String variable) {
        super(MatchingTag.class, forVariable(variable));
    }

    public QMatchingTag(Path<? extends MatchingTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMatchingTag(PathMetadata metadata) {
        super(MatchingTag.class, metadata);
    }

}

