package com.ll.TeamSteam.domain.chatRoom.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = 142843825L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final com.ll.TeamSteam.global.baseEntity.QBaseEntity _super = new com.ll.TeamSteam.global.baseEntity.QBaseEntity(this);

    public final ListPath<com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage, com.ll.TeamSteam.domain.chatMessage.entity.QChatMessage> chatMessages = this.<com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage, com.ll.TeamSteam.domain.chatMessage.entity.QChatMessage>createList("chatMessages", com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage.class, com.ll.TeamSteam.domain.chatMessage.entity.QChatMessage.class, PathInits.DIRECT2);

    public final SetPath<com.ll.TeamSteam.domain.chatUser.entity.ChatUser, com.ll.TeamSteam.domain.chatUser.entity.QChatUser> chatUsers = this.<com.ll.TeamSteam.domain.chatUser.entity.ChatUser, com.ll.TeamSteam.domain.chatUser.entity.QChatUser>createSet("chatUsers", com.ll.TeamSteam.domain.chatUser.entity.ChatUser.class, com.ll.TeamSteam.domain.chatUser.entity.QChatUser.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.ll.TeamSteam.domain.matching.entity.QMatching matching;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final StringPath name = createString("name");

    public final com.ll.TeamSteam.domain.user.entity.QUser owner;

    public QChatRoom(String variable) {
        this(ChatRoom.class, forVariable(variable), INITS);
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoom(PathMetadata metadata, PathInits inits) {
        this(ChatRoom.class, metadata, inits);
    }

    public QChatRoom(Class<? extends ChatRoom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.matching = inits.isInitialized("matching") ? new com.ll.TeamSteam.domain.matching.entity.QMatching(forProperty("matching"), inits.get("matching")) : null;
        this.owner = inits.isInitialized("owner") ? new com.ll.TeamSteam.domain.user.entity.QUser(forProperty("owner"), inits.get("owner")) : null;
    }

}

