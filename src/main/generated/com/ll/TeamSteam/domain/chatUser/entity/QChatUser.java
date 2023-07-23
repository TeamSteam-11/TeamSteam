package com.ll.TeamSteam.domain.chatUser.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatUser is a Querydsl query type for ChatUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatUser extends EntityPathBase<ChatUser> {

    private static final long serialVersionUID = -609053295L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatUser chatUser = new QChatUser("chatUser");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final ListPath<com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage, com.ll.TeamSteam.domain.chatMessage.entity.QChatMessage> chatMessages = this.<com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage, com.ll.TeamSteam.domain.chatMessage.entity.QChatMessage>createList("chatMessages", com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage.class, com.ll.TeamSteam.domain.chatMessage.entity.QChatMessage.class, PathInits.DIRECT2);

    public final com.ll.TeamSteam.domain.chatRoom.entity.QChatRoom chatRoom;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final EnumPath<ChatUserType> type = createEnum("type", ChatUserType.class);

    public final com.ll.TeamSteam.domain.user.entity.QUser user;

    public QChatUser(String variable) {
        this(ChatUser.class, forVariable(variable), INITS);
    }

    public QChatUser(Path<? extends ChatUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatUser(PathMetadata metadata, PathInits inits) {
        this(ChatUser.class, metadata, inits);
    }

    public QChatUser(Class<? extends ChatUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new com.ll.TeamSteam.domain.chatRoom.entity.QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.user = inits.isInitialized("user") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

